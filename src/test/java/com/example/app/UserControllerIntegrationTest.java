package com.example.app;

import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Autowired
    public UserControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testAuthenticatedEmployeeAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testGetAllUsersAsEmployee() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testAddUserAsEmployeeUnauthorized() throws Exception {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("password");

        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testUpdateUserAsEmployeeUnauthorized() throws Exception {
        User updatedUser = new User();
        updatedUser.setUserName("updatedUser");
        updatedUser.setPassword("password");


        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testAddUserAsManager() throws Exception {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setPassword("password");

        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testUpdateUserAsManager() throws Exception {
        User updatedUser = new User();
        updatedUser.setUserName("updatedUser");
        updatedUser.setPassword("password");


        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("updatedUser"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testGetUserByIdAsEmployee() throws Exception {
        // Arrange
        Long userId = 1L;
        UserResponse mockUser = UserResponse.builder().build();
        mockUser.setId(userId);
        mockUser.setUserName("testUser");

        when(userService.getUserById(userId)).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetUserByIdNotFound() throws Exception {
        Long userId = 2L;

        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorMessage").value("User not found with ID: " + userId));

    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testDeleteUserAsEmployeeUnauthorized() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testDeleteUserAsManagerUnauthorized() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserAsAdmin() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}
