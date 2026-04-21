package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.BaseControllerTest;
import com.bifee.projectmanagement.management.application.dto.comment.CommentRequest;
import com.bifee.projectmanagement.management.application.dto.project.CreateProjectRequest;
import com.bifee.projectmanagement.management.application.dto.task.CreateTaskRequest;
import com.bifee.projectmanagement.management.application.dto.task.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerIntegrationTest extends BaseControllerTest {

    @Test
    void shouldManageTaskAndCommentsSuccessfully() throws Exception {
        String token = registerAndGetToken("Task Master", "master@example.com", "Password123@");

        // 1. Create a project
        CreateProjectRequest projectRequest = new CreateProjectRequest("Task Project", "Desc");
        String projectBody = mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long projectId = objectMapper.readTree(projectBody).get("id").asLong();

        // 2. Create a task
        CreateTaskRequest taskRequest = new CreateTaskRequest("New Task", "Task Desc", TaskStatus.TO_DO, TaskPriority.HIGH, Collections.emptySet());
        String taskBody = mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long taskId = objectMapper.readTree(taskBody).get("id").asLong();

        // 3. Get task by ID
        mockMvc.perform(get("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));

        // 4. Update task
        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Updated Task", null, TaskPriority.CRITICAL, TaskStatus.IN_PROGRESS, null);
        mockMvc.perform(patch("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // 5. Add comment
        CommentRequest commentRequest = new CommentRequest("First comment");
        String commentAddedBody = mockMvc.perform(post("/api/tasks/" + taskId + "/comments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        // TaskController#addCommentToTask returns TaskResponse. 
        // Let's assume TaskResponse has a list of comments or we can fetch them separately.
        
        // 6. Get comments
        mockMvc.perform(get("/api/tasks/" + taskId + "/comments")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("First comment"));

        // 7. Get comment count
        mockMvc.perform(get("/api/tasks/" + taskId + "/comments/count")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));

        // 8. Get specific comment
        // Need ID from somewhere. Usually CommentResponse has it.
        String commentsBody = mockMvc.perform(get("/api/tasks/" + taskId + "/comments")
                .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        Long commentId = objectMapper.readTree(commentsBody).get(0).get("id").asLong();

        mockMvc.perform(get("/api/tasks/" + taskId + "/comments/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("First comment"));

        // 9. Update comment
        CommentRequest updateCommentRequest = new CommentRequest("Updated comment");
        mockMvc.perform(patch("/api/tasks/" + taskId + "/comments/" + commentId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCommentRequest)))
                .andExpect(status().isOk());

        // 10. Remove comment
        mockMvc.perform(delete("/api/tasks/" + taskId + "/comments/" + commentId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 11. Delete task
        mockMvc.perform(delete("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
