package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record CreateTaskRequest(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Set<Long> assignedUsersId
) {
}
