package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.task.TaskResponse;
import com.bifee.projectmanagement.management.domain.project.ProjectResponse;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerIntegrationTest extends BaseControllerTest {

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        String token = registerAndGetToken("Task Creator", "creator@example.com", "password123");

        // 1. Create a project first
        CreateProjectRequest projectRequest = new CreateProjectRequest("Project for Task", "Desc");
        MvcResult projectResult = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // We need to get the project ID from the response. 
        // ProjectResponse isn't a record, but we can parse it.
        String projectJson = projectResult.getResponse().getContentAsString();
        Long projectId = objectMapper.readTree(projectJson).get("id").asLong();

        // 2. Create a task in that project
        CreateTaskRequest taskRequest = new CreateTaskRequest("New Task", "Task Desc", TaskPriority.HIGH);

        mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }
}
