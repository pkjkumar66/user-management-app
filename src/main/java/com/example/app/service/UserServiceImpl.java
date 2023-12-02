package com.example.app.service;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserErrorResponse;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.AccessDeniedException;
import com.example.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> response = users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .password(user.getPassword())
                        .build())
                .collect(Collectors.toList());
        return response;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        UserResponse response = UserResponse.builder().build();
        if (user.isPresent()) {
            User existingUser = user.get();
            response.setId(existingUser.getId());
            response.setUserName(existingUser.getUserName());
            response.setPassword(existingUser.getPassword());
        } else {
            UserErrorResponse error = UserErrorResponse.builder()
                    .errorCode("404")
                    .errorMessage("User not found with ID: " + userId)
                    .build();
            response.setError(error);
        }
        return response;

    }

    @Override
    public UserResponse addUser(UserDto userDto) {
        UserResponse response = UserResponse.builder().build();
        try {
            checkAuthorizationForRole("MANAGER", "ADMIN");
            Assert.notNull(userDto, "userDto can't be null");
            Assert.notNull(userDto.getUserName(), "userName can't be null");
            Assert.notNull(userDto.getPassword(), "password can't be null");

            User user = new User(userDto.getUserName(), userDto.getPassword());
            User savedUser = userRepository.save(user);
            response.setId(savedUser.getId());
            response.setUserName(savedUser.getUserName());
            response.setPassword(savedUser.getPassword());
        } catch (Exception e) {
            UserErrorResponse error = UserErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .build();
            response.setError(error);
        }
        return response;

    }

    public UserResponse updateUser(Long userId, UserDto userDto) {
        UserResponse response = UserResponse.builder().build();
        try {
            checkAuthorizationForRole("MANAGER", "ADMIN");
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                if (Objects.nonNull(userDto) && userDto.getUserName().length() > 0) {
                    existingUser.setUserName(userDto.getUserName());
                }

                if (Objects.nonNull(userDto) && userDto.getPassword().length() > 0) {
                    existingUser.setPassword(userDto.getPassword());
                }

                User savedUser = userRepository.save(existingUser);
                response.setId(savedUser.getId());
                response.setUserName(savedUser.getUserName());
                response.setPassword(savedUser.getPassword());
            } else {
                UserErrorResponse error = UserErrorResponse.builder()
                        .errorCode("404")
                        .errorMessage("User not found with ID: " + userId)
                        .build();
                response.setError(error);
            }
        } catch (AccessDeniedException e) {
            UserErrorResponse error = UserErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .build();
            response.setError(error);
        }
        return response;
    }

    @Override
    public UserResponse deleteUserById(Long userId) {
        UserResponse response = UserResponse.builder().build();
        try {
            checkAuthorizationForRole("ADMIN");
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                userRepository.deleteById(userId);
            } else {
                UserErrorResponse error = UserErrorResponse.builder()
                        .errorCode("404")
                        .errorMessage("User not found with ID: " + userId)
                        .build();
                response.setError(error);
            }
        } catch (AccessDeniedException e) {
            UserErrorResponse error = UserErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .build();
            response.setError(error);
        }

        return response;
    }

    private void checkAuthorizationForRole(String... allowedRoles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (String allowedRole : allowedRoles) {
            if (authentication.getAuthorities().stream().anyMatch(authority ->
                    authority.getAuthority().equals("ROLE_" + allowedRole))) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied. User does not have the required role.");
    }
}






