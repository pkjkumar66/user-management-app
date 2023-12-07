package com.example.app;

import com.example.app.dto.UserDto;
import com.example.app.dto.UserResponse;
import com.example.app.mapper.UserMapper;
import com.example.app.service.UserServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private final UserServiceImpl userService;

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
    @WithMockUser(roles = "USER")
    public void testAuthenticatedUSERAccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsersAsRUSER() throws Exception {
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddUserAsUserUnauthorized() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserName("newUser");
        userDto.setPassword("password");

        ResultActions resultActions = mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateUserAsUSERUnauthorized() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setUserName("updatedUser");

        ResultActions resultActions = mockMvc.perform(put("/api/v1/users/update/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDto)));

        resultActions.andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteUserAsUSERUnauthorized() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUserAsADMIN() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserName("newUser");
        userDto.setPassword("password");

        ResultActions resultActions = mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserAsADMIN() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setUserName("newUser");
        userDto.setPassword("password");

        // save user in the context
        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        userDto.setUserName("updatedUser");

        ResultActions resultActions = mockMvc.perform(put("/api/v1/users/update/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDto)));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userName").value("updatedUser"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserByIdAsADMIN() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserName("newUser");
        userDto.setPassword("password");
        Long userId = 1L;

        // save user in the context
        ResultActions addUser = mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));
        addUser.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());

        // get user from the context
        mockMvc.perform(get("/api/v1/users/{userId}", userId)
                        .with(user("admin").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserAsADMINNotFound() throws Exception {
        Long userId = 1L;
        when(userService.deleteUserById(userId)).thenReturn(any(UserResponse.class));

        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserAsADMIN() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserName("newUser");
        userDto.setPassword("password");
        Long userId = 1L;

        // save user in the context
        ResultActions addUser = mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));
        addUser.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());

        // delete user from the context
        mockMvc.perform(delete("/api/v1/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetUserByIdAsUserNotFound() throws Exception {
        Long userId = 2L;
        when(userService.getUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }
}