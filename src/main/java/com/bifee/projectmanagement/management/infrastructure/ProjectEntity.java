package com.bifee.projectmanagement.management.infrastructure;


import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projects")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;
    private Long ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    @ElementCollection
    @CollectionTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "user_id")
    private Set<Long> membersIds;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    private List<TaskEntity> tasks;
    
    protected ProjectEntity() {
    }

    public ProjectEntity(Long id, String title, String description, ProjectStatus projectStatus, Long ownerId, Instant createdAt, Instant updatedAt, Set<Long> membersIds, List<TaskEntity> tasks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.membersIds = membersIds;
        this.tasks = tasks;
    }
    
    protected static ProjectEntity toEntity(Project project){
        return new ProjectEntity(
                project.id(),
                project.title(),
                project.description(),
                project.projectStatus(),
                project.ownerId(),
                project.createdAt(),
                project.updatedAt(),
                project.membersIds(),
                project.tasks().stream()
                        .map(TaskEntity::toEntity)
                        .toList()
        );
    }

    protected static Project toDomain(ProjectEntity projectEntity) {
        return new Project.Builder()
                .withId(projectEntity.id)
                .withTitle(projectEntity.title)
                .withDescription(projectEntity.description)
                .withProjectStatus(projectEntity.projectStatus)
                .withOwnerId(projectEntity.ownerId)
                .withCreatedAt(projectEntity.createdAt)
                .withUpdatedAt(projectEntity.updatedAt)
                .withMembersIds(projectEntity.membersIds)
                .withTasks(projectEntity.tasks.stream()
                        .map(TaskEntity::toDomain)
                        .toList())
                .build();
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

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Set<Long> getMembersIds() {
        return membersIds;
    }

    public List<TaskEntity> getTasks() {
        return tasks;
    }
}

