package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.identity.infrastructure.security.SecurityConfig;
import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.application.TaskService;
import com.bifee.projectmanagement.management.application.dto.comment.CommentResponse;
import com.bifee.projectmanagement.management.application.dto.task.TaskResponse;
import com.bifee.projectmanagement.management.application.dto.comment.CommentRequest;
import com.bifee.projectmanagement.management.application.dto.task.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Endpoints for managing tasks")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class TaskController {
    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID", description = "Returns a single task by its ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public TaskResponse getTaskById(@PathVariable Long taskId){
        Task task = taskService.getTaskById(taskId);
        return TaskResponse.from(task);
    }

    @PatchMapping("/{taskId}")
    @Operation(summary = "Update task by ID", description = "Updates a task by its ID with the provided details")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public TaskResponse updateTaskById(@PathVariable Long taskId, @RequestBody @Valid UpdateTaskRequest request,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.updateTask(taskId, request, requesterId);
        return TaskResponse.from(task);
    }



    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task by ID", description = "Deletes a task by its ID")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public void deleteTaskById(@PathVariable Long taskId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        taskService.deleteTask(taskId, requesterId);
    }

    //REQUEST BODY TEM Q SER  OBJ?
    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a comment to a task", description = "Adds a comment to a task by its ID")
    @ApiResponse(responseCode = "201", description = "Comment added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Task or comment not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public TaskResponse addCommentToTask(@PathVariable Long taskId, @RequestBody @Valid CommentRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.addComment(taskId, request, requesterId);
        return TaskResponse.from(task);
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    @Operation(summary = "Remove a comment from a task", description = "Removes a comment from a task by its ID")
    @ApiResponse(responseCode = "204", description = "Comment removed successfully")
    @ApiResponse(responseCode = "404", description = "Task or comment not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public TaskResponse removeCommentFromTask(@PathVariable Long taskId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.removeComment(taskId, commentId, requesterId);
        return TaskResponse.from(task);
    }

    @PatchMapping("/{taskId}/comments/{commentId}")
    @Operation(summary = "Update a comment on a task", description = "Updates a comment on a task by its ID with the provided details")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Task or comment not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public TaskResponse updateComment(@PathVariable Long taskId, @PathVariable Long commentId,
                                      @RequestBody @Valid CommentRequest request,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.updateComment(taskId, commentId, request, requesterId);
        return TaskResponse.from(task);
    }


    @GetMapping("/{taskId}/comments")
    @Operation(summary = "Get comments for a task", description = "Returns a list of comments for a task by its ID")
    @ApiResponse(responseCode = "200", description = "Comments found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public List<CommentResponse> getCommentsByTaskId(@PathVariable Long taskId) {
        List<Comment> comments = taskService.getCommentsByTaskId(taskId);
        return CommentResponse.fromList(comments);
    }

    @GetMapping("/{taskId}/comments/{commentId}")
    @Operation(summary = "Get a comment by ID", description = "Returns a single comment by its ID")
    @ApiResponse(responseCode = "200", description = "Comment found")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public CommentResponse getCommentById(@PathVariable Long taskId, @PathVariable Long commentId){
        Comment comment = taskService.getCommentById(taskId, commentId);
        return CommentResponse.from(comment);
    }

    @GetMapping("/{taskId}/comments/count")
    @Operation(summary = "Get the number of comments for a task", description = "Returns the number of comments for a task by its ID")
    @ApiResponse(responseCode = "200", description = "Number of comments found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    public int getTaskCommentsCounts(@PathVariable Long taskId){
        return taskService.getTaskCommentsCount(taskId);
    }
}
