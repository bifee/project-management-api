package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;

import java.util.Set;

public record UpdateTaskRequest(
        String title,
        String description,
        TaskPriority priority,
        TaskStatus status,
        Set<Long> assignedUsersIds
) {
}
