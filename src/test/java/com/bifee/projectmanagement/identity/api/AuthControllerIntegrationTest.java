package com.bifee.projectmanagement.identity.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends BaseControllerTest {

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("Test User", "test@example.com", "Password123@", UserRole.DEV);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("Test User"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // First register
        registerAndGetToken("Login User", "login@example.com", "Password123@");

        UserLoginRequest loginRequest = new UserLoginRequest("login@example.com", "Password123@");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        UserLoginRequest loginRequest = new UserLoginRequest("nonexistent@example.com", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailRegistrationWithDuplicateEmail() throws Exception {
        registerAndGetToken("User 1", "duplicate@example.com", "Password123@");

        UserRegistrationRequest request = new UserRegistrationRequest("User 2", "duplicate@example.com", "Password123@", UserRole.DEV);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldFailRegistrationWithInvalidData() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("", "invalid-email", "", null);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
