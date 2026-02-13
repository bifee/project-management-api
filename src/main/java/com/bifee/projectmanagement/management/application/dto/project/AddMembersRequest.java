package com.bifee.projectmanagement.management.application.dto.project;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AddMembersRequest(
        @NotNull(message = "User IDs cannot be null")
        @NotEmpty(message = "At least one user ID is required")
        Set<@NotNull Long> userIds
) {
}
