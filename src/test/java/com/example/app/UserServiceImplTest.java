package com.example.app;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.UserNotFoundException;
import com.example.app.mapper.UserMapper;
import com.example.app.repository.UserRepository;
import com.example.app.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mocking the authentication context
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(TestHelper.USER_ADMIN, TestHelper.USER_ADMIN_PASSWORD,
                        Collections.singleton((GrantedAuthority) () -> TestHelper.ROLE_ADMIN));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    void testAddUser() {
        // Mocking userMapper behavior
        when(userMapper.userDtoToUser(any(UserDto.class))).thenReturn(TestHelper.USER);

        // Mocking userRepository behavior
        when(userRepository.save(any(User.class))).thenReturn(TestHelper.USER);

        // Testing the addUser method
        assertNotNull(userService.addUser(TestHelper.USER_DTO));
        assertEquals(TestHelper.USER_NAME, userService.addUser(TestHelper.USER_DTO).getUserName());
    }

    @Test
    void testGetAllUsers() {
        // Mocking userRepository behavior
        when(userRepository.findAll()).thenReturn(Collections.singletonList(TestHelper.USER));

        // Testing the getAllUsers method
        assertNotNull(userService.getAllUsers());
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    void testGetUserById() {
        // Mocking userRepository behavior
        when(userRepository.findById(TestHelper.USER_ID))
                .thenReturn(Optional.of(new User(TestHelper.USER_NAME, TestHelper.PASSWORD)));

        // Testing the getUserById method
        assertNotNull(userService.getUserById(TestHelper.USER_ID));
        assertEquals(TestHelper.USER_NAME, userService.getUserById(TestHelper.USER_ID).getUserName());

    }

    @Test
    public void testUpdateUser() {
        // Mocking repository behavior
        when(userRepository.findById(TestHelper.USER_ID)).thenReturn(Optional.of(TestHelper.USER));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mocking mapper behavior
        when(userMapper.userDtoToUser(TestHelper.USER_DTO)).thenReturn(TestHelper.USER);

        // Act
        UserResponse response = userService.updateUser(TestHelper.USER_ID, TestHelper.UPDATED_USER_DTO);

        // Assert
        assertNotNull(response);
        assertEquals(TestHelper.UPDATED_USER_NAME, response.getUserName());
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        // Mocking repository behavior for a non-existing user
        when(userRepository.findById(TestHelper.USER_ID)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(TestHelper.USER_ID, TestHelper.USER_DTO));
    }

    @Test
    public void testDeleteUserById() {
        // Mocking repository behavior
        when(userRepository.findById(TestHelper.USER_ID)).thenReturn(Optional.of(TestHelper.USER));

        // Act
        UserResponse response = userService.deleteUserById(TestHelper.USER_ID);

        // Assert
        assertNotNull(response);
        assertEquals(TestHelper.USER_ID, response.getId());
    }

    @Test
    public void testDeleteUserById_UserNotFound() {
        // Mocking repository behavior for a non-existing user
        when(userRepository.findById(TestHelper.USER_ID)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(TestHelper.USER_ID));
    }


}
