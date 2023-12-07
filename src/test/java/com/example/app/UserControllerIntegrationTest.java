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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

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
        mockMvc.perform(get(TestHelper.GET_ALL_USERS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAuthenticatedUSERAccess() throws Exception {
        mockMvc.perform(get(TestHelper.GET_ALL_USERS_URL))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsersAsRUSER() throws Exception {
        mockMvc.perform(get(TestHelper.GET_ALL_USERS_URL))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddUserAsUserUnauthorized() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(TestHelper.ADD_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestHelper.USER_DTO)));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateUserAsUSERUnauthorized() throws Exception {
        ResultActions resultActions = mockMvc.perform(put(TestHelper.UPDATE_USER_URL, TestHelper.USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(TestHelper.USER_DTO)));

        resultActions.andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteUserAsUSERUnauthorized() throws Exception {
        mockMvc.perform(delete(TestHelper.GET_USER_URL, TestHelper.USER_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUserAsADMIN() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(TestHelper.ADD_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestHelper.USER_DTO)));

        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserAsADMIN() throws Exception {
        // save user in the context
        ResultActions addUser = mockMvc.perform(post(TestHelper.ADD_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestHelper.USER_DTO)));
        addUser.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());

        UserResponse response =
                objectMapper.readValue(addUser.andReturn().getResponse().getContentAsString(), UserResponse.class);

        // delete user from the context
        mockMvc.perform(delete(TestHelper.DELETE_USER_URL, response.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUserAsADMIN() throws Exception {
        UserDto userDto = TestHelper.USER_DTO;

        // save user in the context
        mockMvc.perform(post("/api/v1/users/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestHelper.USER_DTO)));

        userDto.setUserName("updatedUser");

        ResultActions resultActions = mockMvc.perform(put(TestHelper.UPDATE_USER_URL, TestHelper.USER_ID)
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
        // save user in the context
        MvcResult addUserResult = mockMvc.perform(post(TestHelper.ADD_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestHelper.USER_DTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        UserResponse response = objectMapper.readValue(
                addUserResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        // get user from the context
        mockMvc.perform(get(TestHelper.GET_USER_URL, response.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserAsADMINNotFound() throws Exception {
        mockMvc.perform(delete(TestHelper.DELETE_USER_URL, TestHelper.USER_ID_2))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetUserByIdAsUserNotFound() throws Exception {
        mockMvc.perform(get(TestHelper.GET_USER_URL, TestHelper.USER_ID_2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}