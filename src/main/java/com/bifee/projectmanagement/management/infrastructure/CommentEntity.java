package com.bifee.projectmanagement.management.infrastructure;


import com.bifee.projectmanagement.management.domain.comment.Comment;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "comments")
class CommentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long creatorId;
    private Long taskId;
    private Instant createdAt;
    private Instant updatedAt;

    public CommentEntity() {
    }

    public CommentEntity(Long id, String content, Long creatorId, Long taskId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.content = content;
        this.creatorId = creatorId;
        this.taskId = taskId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected static Comment toDomain(CommentEntity commentEntity){
        return new Comment.Builder()
                .withId(commentEntity.getId())
                .withContent(commentEntity.getContent())
                .withCreator(commentEntity.getCreatorId())
                .withTask(commentEntity.getTaskId())
                .withCreatedAt(commentEntity.getCreatedAt())
                .withUpdatedAt(commentEntity.getUpdatedAt())
                .build();
    }

    protected static CommentEntity toEntity(Comment comment){
        return new CommentEntity(comment.id(), comment.content() , comment.creatorId(), comment.taskId(), comment.createdAt(), comment.updatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

