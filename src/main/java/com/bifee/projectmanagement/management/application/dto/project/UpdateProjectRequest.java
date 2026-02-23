package com.bifee.projectmanagement.management.application.dto.project;

import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @Size(max = 50)
        String title,

        @Size(max = 2000)
        String description,
        ProjectStatus status
) {
}
