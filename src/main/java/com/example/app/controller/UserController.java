package com.example.app.controller;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserResponse;
import com.example.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Get a specific user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user));
    }

    // Update an existing user
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserDto user) {
        return ResponseEntity.ok(userService.updateUser(userId, user));
    }

    // Delete a user
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUserById(userId));
    }
}