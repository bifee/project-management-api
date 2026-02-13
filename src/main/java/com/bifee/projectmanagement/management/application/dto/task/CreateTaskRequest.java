package com.bifee.projectmanagement.management.application.dto.task;

import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;

import java.util.Set;

public record CreateTaskRequest(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Set<Long> assignedUsersId
) {
}
