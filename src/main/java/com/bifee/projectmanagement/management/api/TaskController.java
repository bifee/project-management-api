package com.bifee.projectmanagement.management.api;

import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.management.application.TaskService;
import com.bifee.projectmanagement.management.application.dto.CommentResponse;
import com.bifee.projectmanagement.management.application.dto.TaskResponse;
import com.bifee.projectmanagement.management.application.dto.UpdateTaskRequest;
import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.Task;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/task")
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

    @GetMapping("/{taskId}/comment")
    public TaskResponse addCommentToTask(@PathVariable Long taskId, @RequestBody String content, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.addComment(taskId, content, requesterId);
        return TaskResponse.from(task);
    }

    @DeleteMapping("/{taskId}/comment/{commentId}")
    public TaskResponse removeCommentFromTask(@PathVariable Long taskId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.removeComment(taskId, commentId, requesterId);
        return TaskResponse.from(task);
    }

    @PatchMapping("/{taskId}/comment/{commentId}")
    public TaskResponse updateComment(@PathVariable Long taskId, @PathVariable Long commentId,
                                      @RequestBody String content,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        Task task = taskService.updateComment(taskId, commentId, content, requesterId);
        return TaskResponse.from(task);
    }


    @GetMapping("/{taskId}/comment")
    public List<CommentResponse> getCommentsByTaskId(@PathVariable Long taskId) {
        List<Comment> comments = taskService.getCommentsByTaskId(taskId);
        return CommentResponse.fromList(comments);
    }

    @GetMapping("/{taskId}/comment/{commentId}")
    public CommentResponse getCommentById(@PathVariable Long taskId, @PathVariable Long commentId){
        Comment comment = taskService.getCommentById(taskId, commentId);
        return CommentResponse.from(comment);
    }

    @GetMapping("/{taskId}/comment/count")
    public int getTaskCommentsCounts(@PathVariable Long taskId){
        return taskService.getTaskCommentsCount(taskId);
    }
}
