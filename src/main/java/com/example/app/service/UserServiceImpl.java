package com.example.app.service;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.AccessDeniedException;
import com.example.app.exception.UserNotFoundException;
import com.example.app.mapper.UserMapper;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        checkAuthorizationForRole("USER", "ADMIN");
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
        checkAuthorizationForRole("USER", "ADMIN");
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return UserResponse.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .build();
    }

    @Override
    public UserResponse addUser(UserDto userDto) {
        checkAuthorizationForRole("ADMIN");
        validateUserDto(userDto);

        User user = userMapper.userDtoToUser(userDto);
        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .build();

    }

    private void validateUserDto(UserDto userDto) {
        Assert.notNull(userDto, "userDto can't be null");
        Assert.notNull(userDto.getUserName(), "userName can't be null");
        Assert.notNull(userDto.getPassword(), "password can't be null");
    }

    public UserResponse updateUser(Long userId, UserDto userDto) {
        checkAuthorizationForRole("ADMIN");
        Assert.notNull(userDto, "userDto can't be null");

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            updateIfPresent(userDto.getUserName(), existingUser::setUserName);
            updateIfPresent(userDto.getPassword(), existingUser::setPassword);

            User savedUser = userRepository.save(existingUser);

            return UserResponse.builder()
                    .id(savedUser.getId())
                    .userName(savedUser.getUserName())
                    .build();
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    private void updateIfPresent(String newValue, Consumer<String> updater) {
        if (Objects.nonNull(newValue) && !newValue.isEmpty()) {
            updater.accept(newValue);
        }
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






