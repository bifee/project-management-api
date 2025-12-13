package com.bifee.projectmanagement.dto;

import com.bifee.projectmanagement.entity.Comment;
import com.bifee.projectmanagement.entity.Task;
import com.bifee.projectmanagement.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;

    private String content;

    private UserResponseDTO creator;

    private TaskResponseDTO task;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.creator = new UserResponseDTO(comment.getCreator());
        this.task = new TaskResponseDTO(comment.getTask());
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
