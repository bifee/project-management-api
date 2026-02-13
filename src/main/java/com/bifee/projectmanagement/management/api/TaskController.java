package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.application.TaskService;
import com.bifee.projectmanagement.management.application.dto.CommentResponse;
import com.bifee.projectmanagement.management.application.dto.TaskResponse;
import com.bifee.projectmanagement.management.application.dto.CommentRequest;
import com.bifee.projectmanagement.management.application.dto.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.Task;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTaskById(@PathVariable Long taskId){
        Task task = taskService.getTaskById(taskId);
        return TaskResponse.from(task);
    }

    @PatchMapping("/{taskId}")
    public TaskResponse updateTaskById(@PathVariable Long taskId, @RequestBody @Valid UpdateTaskRequest request,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.updateTask(taskId, request, requesterId);
        return TaskResponse.from(task);
    }



    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable Long taskId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        taskService.deleteTask(taskId, requesterId);
    }

    //REQUEST BODY TEM Q SER  OBJ?
    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse addCommentToTask(@PathVariable Long taskId, @RequestBody @Valid CommentRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.addComment(taskId, request, requesterId);
        return TaskResponse.from(task);
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public TaskResponse removeCommentFromTask(@PathVariable Long taskId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.removeComment(taskId, commentId, requesterId);
        return TaskResponse.from(task);
    }

    @PatchMapping("/{taskId}/comments/{commentId}")
    public TaskResponse updateComment(@PathVariable Long taskId, @PathVariable Long commentId,
                                      @RequestBody @Valid CommentRequest request,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.updateComment(taskId, commentId, request, requesterId);
        return TaskResponse.from(task);
    }


    @GetMapping("/{taskId}/comments")
    public List<CommentResponse> getCommentsByTaskId(@PathVariable Long taskId) {
        List<Comment> comments = taskService.getCommentsByTaskId(taskId);
        return CommentResponse.fromList(comments);
    }

    @GetMapping("/{taskId}/comments/{commentId}")
    public CommentResponse getCommentById(@PathVariable Long taskId, @PathVariable Long commentId){
        Comment comment = taskService.getCommentById(taskId, commentId);
        return CommentResponse.from(comment);
    }

    @GetMapping("/{taskId}/comments/count")
    public int getTaskCommentsCounts(@PathVariable Long taskId){
        return taskService.getTaskCommentsCount(taskId);
    }
}
