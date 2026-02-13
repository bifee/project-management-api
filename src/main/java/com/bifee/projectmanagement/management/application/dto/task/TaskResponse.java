package com.bifee.projectmanagement.management.application.dto.task;

import com.bifee.projectmanagement.management.application.dto.comment.CommentResponse;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Set<Long> assignedUsersId,
        Long projectId,
        List<CommentResponse> comments,
        Instant createdAt,
        Instant updatedAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.id(),
                task.title(),
                task.description(),
                task.status(),
                task.priority(),
                task.assignedUsersId(),
                task.projectId(),
                task.comments() != null ?
                        task.comments().stream()
                        .map(CommentResponse::from)
                        .toList()
                        : List.of(),
                task.createdAt(),
                task.updatedAt()
        );
    }

    public static List<TaskResponse> fromList(List<Task> tasks){
        return tasks.stream().map(TaskResponse::from).toList();
    }
}
