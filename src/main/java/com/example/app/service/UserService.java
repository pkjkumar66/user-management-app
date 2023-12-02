package com.example.app.service;


import com.example.app.dto.UserDto;
import com.example.app.dto.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    UserResponse addUser(UserDto user);

    UserResponse updateUser(Long userId, UserDto user);

    UserResponse deleteUserById(Long userId);

}
