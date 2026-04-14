package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerIntegrationTest extends BaseControllerTest {

    @Test
    void shouldCreateProjectSuccessfully() throws Exception {
        String token = registerAndGetToken("Project Owner", "owner@example.com", "password123");

        CreateProjectRequest request = new CreateProjectRequest("New Project", "Project Description");

        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Project"))
                .andExpect(jsonPath("$.description").value("Project Description"));
    }

    @Test
    void shouldGetMyProjectsSuccessfully() throws Exception {
        String token = registerAndGetToken("User A", "user_a@example.com", "password123");

        CreateProjectRequest request = new CreateProjectRequest("Project A", "Desc A");
        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Project A"));
    }

    @Test
    void shouldFailToCreateProjectWithBlankTitle() throws Exception {
        String token = registerAndGetToken("User B", "user_b@example.com", "password123");

        CreateProjectRequest request = new CreateProjectRequest("", "Desc");

        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
