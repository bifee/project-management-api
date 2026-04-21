package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.management.application.dto.project.AddMembersRequest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.project.UpdateProjectRequest;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerIntegrationTest extends BaseControllerTest {

    @Test
    void shouldCreateProjectSuccessfully() throws Exception {
        String token = registerAndGetToken("Project Owner", "owner@example.com", "Password123@");

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
    void shouldGetProjectByIdSuccessfully() throws Exception {
        String token = registerAndGetToken("Getter", "getter@example.com", "Password123@");
        CreateProjectRequest request = new CreateProjectRequest("Get Project", "Desc");
        String body = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        Integer projectId = objectMapper.readTree(body).get("id").asInt();

        mockMvc.perform(get("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.title").value("Get Project"));
    }

    @Test
    void shouldGetProjectsByOwnerIdSuccessfully() throws Exception {
        String token = registerAndGetToken("Owner X", "owner_x@example.com", "Password123@");
        String userBody = mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        Integer userId = objectMapper.readTree(userBody).get("id").asInt();

        CreateProjectRequest request = new CreateProjectRequest("Project X", "Desc X");
        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects/owner/" + userId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Project X"));
    }

    @Test
    void shouldGetMyProjectsSuccessfully() throws Exception {
        String token = registerAndGetToken("User A", "user_a@example.com", "Password123@");

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
    void shouldSearchProjectsByTitleSuccessfully() throws Exception {
        String token = registerAndGetToken("Searcher", "searcher@example.com", "Password123@");
        CreateProjectRequest request = new CreateProjectRequest("Unique Title", "Desc");
        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/projects/search")
                .param("title", "Unique")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Unique Title"));
    }

    @Test
    void shouldUpdateProjectSuccessfully() throws Exception {
        String token = registerAndGetToken("Updater", "updater@example.com", "Password123@");
        CreateProjectRequest createRequest = new CreateProjectRequest("Old Title", "Old Desc");
        String body = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Integer projectId = objectMapper.readTree(body).get("id").asInt();

        UpdateProjectRequest updateRequest = new UpdateProjectRequest("New Title", "New Desc", ProjectStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldDeleteProjectSuccessfully() throws Exception {
        String token = registerAndGetToken("Deleter", "deleter@example.com", "Password123@");
        CreateProjectRequest request = new CreateProjectRequest("Delete Me", "Desc");
        String body = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        Integer projectId = objectMapper.readTree(body).get("id").asInt();

        mockMvc.perform(delete("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/projects/" + projectId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddAndRemoveMembersSuccessfully() throws Exception {
        String ownerToken = registerAndGetToken("Owner", "owner_member@example.com", "Password123@");
        String memberToken = registerAndGetToken("Member", "member@example.com", "Password123@");
        
        // Get member ID
        String memberBody = mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + memberToken))
                .andReturn().getResponse().getContentAsString();
        Long memberId = objectMapper.readTree(memberBody).get("id").asLong();

        // Create project
        CreateProjectRequest createRequest = new CreateProjectRequest("Member Project", "Desc");
        String projectBody = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Integer projectId = objectMapper.readTree(projectBody).get("id").asInt();

        // Add member
        AddMembersRequest addRequest = new AddMembersRequest(Set.of(memberId));
        mockMvc.perform(post("/api/projects/" + projectId + "/members/")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Remove member
        mockMvc.perform(delete("/api/projects/" + projectId + "/members/" + memberId)
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailToCreateProjectWithBlankTitle() throws Exception {
        String token = registerAndGetToken("User B", "user_b@example.com", "Password123@");

        CreateProjectRequest request = new CreateProjectRequest("", "Desc");

        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
