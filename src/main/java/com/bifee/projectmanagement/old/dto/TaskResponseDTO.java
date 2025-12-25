package com.bifee.projectmanagement.old.dto;

import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {
    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private UserResponseDTO assignedUser;

    private ProjectResponseDTO project;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CommentResponseDTO> comments;

    public TaskResponseDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.assignedUser = new UserResponseDTO(task.getAssignedUser());
        this.project = new ProjectResponseDTO(task.getProject());
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.comments = task.getComments().stream().map(CommentResponseDTO::new).collect(Collectors.toList());
    }
}
