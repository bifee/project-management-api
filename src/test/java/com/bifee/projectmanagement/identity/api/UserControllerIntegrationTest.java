package com.bifee.projectmanagement.identity.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends BaseControllerTest {

    @Test
    void shouldGetMyProfileSuccessfully() throws Exception {
        String token = registerAndGetToken("Profile User", "profile@example.com", "Password123@");

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.name").value("Profile User"));
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        String token = registerAndGetToken("User One", "one@example.com", "Password123@");
        String body = mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        Integer id = objectMapper.readTree(body).get("id").asInt();

        mockMvc.perform(get("/api/users/" + id)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("one@example.com"));
    }

    @Test
    void shouldGetAllUsersWhenAdmin() throws Exception {
        registerAndGetToken("Standard User", "standard@example.com", "Password123@");
        String adminToken = registerAndGetToken("Admin User", "admin@example.com", "Admin123@", UserRole.ADMIN);

        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetActiveUsersWhenAdmin() throws Exception {
        String adminToken = registerAndGetToken("Admin Active", "admin_active@example.com", "Admin123@", UserRole.ADMIN);

        mockMvc.perform(get("/api/users/active")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetUsersByRoleWhenAdmin() throws Exception {
        String adminToken = registerAndGetToken("Admin Role", "admin_role@example.com", "Admin123@", UserRole.ADMIN);

        mockMvc.perform(get("/api/users/by-role")
                .param("role", "ADMIN")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void shouldSearchUsersByNameWhenAdmin() throws Exception {
        registerAndGetToken("Search Target", "search@example.com", "Password123@");
        String adminToken = registerAndGetToken("Admin Search", "admin_search@example.com", "Admin123@", UserRole.ADMIN);

        mockMvc.perform(get("/api/users/search")
                .param("name", "Search")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(org.hamcrest.Matchers.containsString("Search")));
    }

    @Test
    void shouldUpdateMyProfileSuccessfully() throws Exception {
        String token = registerAndGetToken("Update User", "update@example.com", "Password123@");

        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest(null, "Updated Name");

        mockMvc.perform(patch("/api/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void shouldUpdateMyPasswordSuccessfully() throws Exception {
        String token = registerAndGetToken("Password User", "password_user@example.com", "Password123@");

        UpdatePasswordRequest updateRequest = new UpdatePasswordRequest("Password123@", "newPassword123@", "newPassword123@");

        mockMvc.perform(patch("/api/users/me/password")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdateUserRoleWhenAdmin() throws Exception {
        String userToken = registerAndGetToken("Role Update", "role_update@example.com", "Password123@");
        String body = mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andReturn().getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(body).get("id").asInt();

        String adminToken = registerAndGetToken("Admin Role Mod", "admin_role_mod@example.com", "Admin123@", UserRole.ADMIN);

        mockMvc.perform(patch("/api/users/" + userId + "/role")
                .param("newRole", "PROJECT_MANAGER")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PROJECT_MANAGER"));
    }

    @Test
    void shouldActivateAndDeactivateUserWhenAdmin() throws Exception {
        String userToken = registerAndGetToken("State User", "state@example.com", "Password123@");
        String body = mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andReturn().getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(body).get("id").asInt();

        String adminToken = registerAndGetToken("Admin State", "admin_state@example.com", "Admin123@", UserRole.ADMIN);

        // Deactivate
        mockMvc.perform(patch("/api/users/" + userId + "/deactivate")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        // Activate
        mockMvc.perform(patch("/api/users/" + userId + "/activate")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void shouldDeactivateMyAccountSuccessfully() throws Exception {
        String token = registerAndGetToken("Deactivate Me", "deactivate_me@example.com", "Password123@");

        mockMvc.perform(patch("/api/users/me/deactivate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void shouldDeleteUserWhenAdmin() throws Exception {
        String userToken = registerAndGetToken("Delete Target", "delete@example.com", "Password123@");
        String body = mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andReturn().getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(body).get("id").asInt();

        String adminToken = registerAndGetToken("Admin Delete", "admin_delete@example.com", "Admin123@", UserRole.ADMIN);

        mockMvc.perform(delete("/api/users/" + userId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFailToGetProfileWhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailGetAllUsersWhenNotAdmin() throws Exception {
        String devToken = registerAndGetToken("Dev User", "dev@example.com", "Dev1234@", UserRole.DEV);

        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + devToken))
                .andExpect(status().isForbidden());
    }
}
