package com.example.app.service;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserErrorResponse;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.AccessDeniedException;
import com.example.app.exception.UserNotFoundException;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(Objects::nonNull)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return UserResponse.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .build();
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
        checkAuthorizationForRole("ADMIN");
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
            return UserResponse.builder().id(userId).build();
        } else {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
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






