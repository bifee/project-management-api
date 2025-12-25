package com.bifee.projectmanagement.management.infrastructure;

import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tasks")
class TaskEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @ElementCollection
    @CollectionTable(name = "task_assigned_users", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "user_id")
    private Set<Long> assignedUsersId;

    @Column(name = "project_id")
    private Long projectId;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "task_id")
    private List<CommentEntity> comments;

    public TaskEntity() {
    }

    public TaskEntity(Long id, String title, String description, TaskStatus status, TaskPriority priority, Set<Long> assignedUsersId, Long projectId, Instant createdAt, Instant updatedAt, List<CommentEntity> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignedUsersId = assignedUsersId;
        this.projectId = projectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.comments = comments;
    }

    protected static Task toDomain(TaskEntity taskEntity){
        return new Task(taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getStatus(),
                taskEntity.getPriority(),
                taskEntity.getAssignedUsersId(),
                taskEntity.getProjectId(),
                taskEntity.getCreatedAt(),
                taskEntity.getUpdatedAt(),
                taskEntity.getComments().stream().map(CommentEntity::toDomain).toList());
    }

    protected static TaskEntity toEntity(Task task){
        return new TaskEntity(task.id(),
                task.title(),
                task.description(),
                task.status(), task.priority(), task.assignedUsersId(), task.projectId(), task.createdAt(), task.updatedAt(), task.comments().stream().map(CommentEntity::toEntity).toList());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Set<Long> getAssignedUsersId() {
        return assignedUsersId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }
}

