package com.bifee.projectmanagement.management.infrastructure;


import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.task.Task;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, foreignKey = @ForeignKey(
            name = "fk_comment_task",
            foreignKeyDefinition = "FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE"
    ))
    private TaskEntity task;
    private Instant createdAt;
    private Instant updatedAt;

    public CommentEntity() {
    }

    public CommentEntity(Long id, String content, Long creatorId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.content = content;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected static Comment toDomain(CommentEntity commentEntity){
        return new Comment.Builder()
                .withId(commentEntity.getId())
                .withContent(commentEntity.getContent())
                .withCreator(commentEntity.getCreatorId())
                .withCreatedAt(commentEntity.getCreatedAt())
                .withUpdatedAt(commentEntity.getUpdatedAt())
                .build();
    }

    protected static CommentEntity toEntity(Comment comment){
        return new CommentEntity(comment.id(), comment.content() , comment.creatorId(), comment.createdAt(), comment.updatedAt());
    }

    void setTask(TaskEntity task) {
        this.task = task;
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

    public TaskEntity getTask() {
        return task;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

