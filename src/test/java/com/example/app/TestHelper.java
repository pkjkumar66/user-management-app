package com.example.app;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;

/**
 * TestHelper class provides predefined values for testing purposes.
 */
public class TestHelper {

    public static final Long USER_ID = 1L;
    public static final String USER_NAME = "user";
    public static final String PASSWORD = "password";
    public static final String UPDATED_USER_NAME = "updated_user";
    public static final String UPDATED_USER_PASSWORD = "updated_password";
    public static final User USER = new User(USER_NAME, PASSWORD);

    public static final UserDto USER_DTO = UserDto.builder()
            .userName(USER_NAME)
            .password(PASSWORD)
            .build();

    public static final UserDto UPDATED_USER_DTO = UserDto.builder()
            .userName(UPDATED_USER_NAME)
            .password(UPDATED_USER_PASSWORD)
            .build();

    public static final UserResponse USER_RESPONSE = UserResponse.builder()
            .id(USER_ID)
            .userName(USER_NAME)
            .build();
}