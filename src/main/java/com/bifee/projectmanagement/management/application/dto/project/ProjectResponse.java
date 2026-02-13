package com.bifee.projectmanagement.management.application.dto.project;

import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        ProjectStatus status,
        Set<Long> membersIds,
        Long ownerId,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.id(),
                project.title(),
                project.description(),
                project.projectStatus(),
                project.membersIds(),
                project.ownerId(),
                project.createdAt(),
                project.updatedAt()
        );
    }

    public static List<ProjectResponse> fromList(List<Project> projects){
        return projects.stream().map(ProjectResponse::from).toList();
    }
}
