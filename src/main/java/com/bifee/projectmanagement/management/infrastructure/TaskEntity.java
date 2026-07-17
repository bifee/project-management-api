package com.bifee.projectmanagement.management.infrastructure;

import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskPriority;
import com.bifee.projectmanagement.management.domain.task.TaskStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private Set<Long> assignedUsersId = new HashSet<>();

    @Column(name = "project_id")
    private Long projectId;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

    public TaskEntity() {
    }

    public TaskEntity(Long id, String title, String description, TaskStatus status, TaskPriority priority, Set<Long> assignedUsersId, Long projectId, Instant createdAt, Instant updatedAt, List<CommentEntity> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        setAssignedUsersId(assignedUsersId);
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
                .withAssignedUser(new HashSet<>(taskEntity.assignedUsersId))
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
        TaskEntity taskEntity = new TaskEntity(
                task.id(),
                task.title(),
                task.description(),
                task.status(),
                task.priority(),
                task.assignedUsersId(),
                task.projectId(),
                task.createdAt(),
                task.updatedAt(),
                null
        );
        
        if(task.comments() != null) {
            List<CommentEntity> commentEntities = task.comments().stream()
                .map(CommentEntity::toEntity)
                .toList();
            taskEntity.setComments(commentEntities);
        }
        return taskEntity;
    }

    void updateFrom(Task task) {
        this.title = task.title();
        this.description = task.description();
        this.status = task.status();
        this.priority = task.priority();
        this.projectId = task.projectId();
        this.createdAt = task.createdAt();
        this.updatedAt = task.updatedAt();
        setAssignedUsersId(task.assignedUsersId());

        List<CommentEntity> updatedComments = reconcileComments(task.comments());
        setComments(updatedComments);
    }


    private void setComments(List<CommentEntity> comments) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        } else {
            new ArrayList<>(this.comments).forEach(this::removeComment);
        }
        if (comments != null) {
            comments.forEach(this::addComment);
        }
    }

    private void setAssignedUsersId(Set<Long> assignedUsersId) {
        this.assignedUsersId.clear();
        if (assignedUsersId != null) {
            this.assignedUsersId.addAll(assignedUsersId);
        }
    }

    public void addComment(CommentEntity comment) {
        this.comments.add(comment);
        comment.setTask(this);
    }

    public void removeComment(CommentEntity comment) {
        this.comments.remove(comment);
        comment.setTask(null);
    }

    private List<CommentEntity> reconcileComments(List<com.bifee.projectmanagement.management.domain.comment.Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }

        Map<Long, CommentEntity> existingCommentsById = new LinkedHashMap<>();
        for (CommentEntity existingComment : this.comments) {
            if (existingComment.getId() != null) {
                existingCommentsById.put(existingComment.getId(), existingComment);
            }
        }

        List<CommentEntity> reconciledComments = new ArrayList<>();
        for (com.bifee.projectmanagement.management.domain.comment.Comment comment : comments) {
            CommentEntity existingComment = comment.id() == null ? null : existingCommentsById.get(comment.id());
            if (existingComment != null) {
                existingComment.updateFrom(comment);
                reconciledComments.add(existingComment);
            } else {
                reconciledComments.add(CommentEntity.toEntity(comment));
            }
        }
        return reconciledComments;
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

