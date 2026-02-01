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
public class TaskEntity {
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

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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
        this.setComments(comments);
    }

    protected static Task toDomain(TaskEntity taskEntity){
        return new Task.Builder()
                .withId(taskEntity.id)
                .withTitle(taskEntity.title)
                .withDescription(taskEntity.description)
                .withStatus(taskEntity.status)
                .withPriority(taskEntity.priority)
                .withAssignedUser(taskEntity.assignedUsersId)
                .withProject(taskEntity.projectId)
                .withCreatedAt(taskEntity.createdAt)
                .withUpdatedAt(taskEntity.updatedAt)
                .withComments(
                        taskEntity.comments.stream()
                                .map(CommentEntity::toDomain)
                                .toList()
                )
                .build();
    }

    protected static TaskEntity toEntity(Task task){
        return new TaskEntity(task.id(),
                task.title(),
                task.description(),
                task.status(), task.priority(), task.assignedUsersId(), task.projectId(), task.createdAt(), task.updatedAt(), task.comments().stream().map(CommentEntity::toEntity).toList());
    }


    private void setComments(List<CommentEntity> comments) {
        this.comments.clear();
        if (comments != null) {
            comments.forEach(this::addComment);
        }
    }

    public void addComment(CommentEntity comment) {
        this.comments.add(comment);
        comment.setTask(this);
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

