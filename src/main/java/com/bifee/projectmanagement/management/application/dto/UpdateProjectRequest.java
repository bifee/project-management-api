package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.project.ProjectStatus;

public record UpdateProjectRequest(
        String title,
        String description,
        ProjectStatus status
) {
}
