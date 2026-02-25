package com.bifee.projectmanagement.management.application.dto.task;

import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateTaskRequest(
        @Size(max = 50)
        @NotBlank
        String title,

        @Size(max = 2000)
        String description,

        @NotNull
        TaskStatus status,

        @NotNull
        TaskPriority priority,

        Set<@NotNull @Positive Long> assignedUsersId
) {
}
