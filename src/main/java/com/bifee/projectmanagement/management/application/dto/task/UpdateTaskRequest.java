package com.bifee.projectmanagement.management.application.dto.task;

import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateTaskRequest(
        @Size(max = 50)
        String title,
        @Size(max = 2000)
        String description,
        TaskPriority priority,
        TaskStatus status,
        Set<Long> assignedUsersIds
) {
}
