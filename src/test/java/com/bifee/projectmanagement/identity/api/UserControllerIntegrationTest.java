package com.bifee.projectmanagement.identity.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@
class UserControllerIntegrationTest{

    @Test
    void shouldGetMyProfileSuccessfully() throws Exception {
        String token = registerAndGetToken("Profile User", "profile@example.com", "password123");

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.name").value("Profile User"));
    }

    @Test
    void shouldUpdateMyProfileSuccessfully() throws Exception {
        String token = registerAndGetToken("Update User", "update@example.com", "password123");

        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest("Updated Name", null);

        mockMvc.perform(patch("/api/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldFailToGetProfileWhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
