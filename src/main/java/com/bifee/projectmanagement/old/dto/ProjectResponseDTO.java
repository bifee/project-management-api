package com.bifee.projectmanagement.old.dto;

import com.bifee.projectmanagement.management.domain.project.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {

    private Long id;

    private String name;

    private String description;

    private ProjectStatus status;

    private UserResponseDTO owner;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<UserResponseDTO> members;

    private List<TaskResponseDTO> tasks;

    public ProjectResponseDTO(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.status = project.getStatus();
        this.owner = new UserResponseDTO(project.getOwner());
        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
        this.members = project.getMembers().stream().map(UserResponseDTO::new).collect(Collectors.toList());

        this.tasks = project.getTasks().stream().map(TaskResponseDTO::new).collect(Collectors.toList());
    }
}
