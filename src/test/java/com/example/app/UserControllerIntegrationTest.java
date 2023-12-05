package com.example.app;

import com.example.app.dto.UserResponse;
import com.example.app.entity.User;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.mapper.UserMapper;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerIntegrationTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testAuthenticatedEmployeeAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testGetAllUsersAsEmployee() throws Exception {
        mockMvc.perform(get("/api/v1/users/all"))
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

        ResultActions resultActions = mockMvc.perform(post("/api/v1/users/add")
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


        ResultActions resultActions = mockMvc.perform(post("/api/v1/users/update/1")
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

        ResultActions resultActions = mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testUpdateUserAsManager() throws Exception {
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(UserResponse.builder().id(userId).build());
        mockMvc.perform(post("/api/v1/users/update/1", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testGetUserByIdNotFound() throws Exception {
        Long userId = 2L;

        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testDeleteUserAsEmployeeUnauthorized() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    public void testDeleteUserAsManagerUnauthorized() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isForbidden());
    }

}
