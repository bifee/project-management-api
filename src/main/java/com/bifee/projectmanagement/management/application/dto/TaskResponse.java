package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;

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
        List<Comment> comments
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
                task.comments()
        );
    }
}
