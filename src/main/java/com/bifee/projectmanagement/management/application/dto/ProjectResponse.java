package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import com.bifee.projectmanagement.management.infrastructure.ProjectEntity;

import java.util.Set;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        ProjectStatus status,
        Set<Long> membersIds,
        Long ownerId
) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.id(),
                project.title(),
                project.description(),
                project.projectStatus(),
                project.membersIds(),
                project.ownerId()
        );
    }
}
