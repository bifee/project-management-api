package com.bifee.projectmanagement.identity.api;

import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.domain.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private String token;

    private Long userId;

    public UserControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.token = "";
    }

    @BeforeEach
    void setUp() throws Exception {
        var adminRegisterRequest = new UserRegistrationRequest(
                "Manager",
                "manager@test.com",
                "Password@123",
                UserRole.ADMIN
        );

        var devRegisterRequest = new UserRegistrationRequest(
                "Developer",
                "dev@test.com",
                "DevPassword@123",
                UserRole.DEV
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRegisterRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devRegisterRequest)))
                .andExpect(status().isCreated());


        var adminLoginRequest = new UserLoginRequest("manager@test.com", "Password@123");
        var devLoginRequest = new UserLoginRequest("dev@test.com", "DevPassword@123");


        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(devLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        this.token = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getMyProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        mockMvc.perform(get("/api/users/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getActiveUsers() throws Exception {
        mockMvc.perform(get("/api/users/active")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByRole() throws Exception {
        mockMvc.perform(get("/api/users/by-role")
                .param("role", "ADMIN")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsersByName() throws Exception {
        mockMvc.perform(get("/api/users/search")
                .param("name", "Manager")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateMyProfile() throws Exception {
        var updateRequest = new UpdateUserProfileRequest("updatedMyProfile@test.com", "Updated Bio");

        mockMvc.perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateProfile() throws Exception {
        var updateRequest = new UpdateUserProfileRequest("updatedmanager@test.com", "Director");

        mockMvc.perform(patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateMyPassword() throws Exception {
        var passwordRequest = new UpdatePasswordRequest("Password@123", "NewPassword@123", "NewPassword@123");

        mockMvc.perform(patch("/api/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void updatePassword() throws Exception {
        var passwordRequest = new UpdatePasswordRequest("Password@123", "NewPassword@123", "NewPassword@123");

        mockMvc.perform(patch("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUserRole() throws Exception {
        mockMvc.perform(patch("/api/users/2/role")
                .param("newRole", "VIEWER")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void activateUser() throws Exception {
        mockMvc.perform(patch("/api/users/2/activate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateUser() throws Exception {
        mockMvc.perform(patch("/api/users/2/deactivate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateMyAccount() throws Exception {
        mockMvc.perform(patch("/api/users/me/deactivate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/2")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}