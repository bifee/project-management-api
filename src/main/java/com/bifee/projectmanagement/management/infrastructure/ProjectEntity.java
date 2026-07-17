package com.bifee.projectmanagement.management.infrastructure;


import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
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
    private Set<Long> membersIds = new HashSet<>();

    protected ProjectEntity() {
    }

    public ProjectEntity(Long id, String title, String description, ProjectStatus projectStatus, Long ownerId, Instant createdAt, Instant updatedAt, Set<Long> membersIds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        setMembersIds(membersIds);
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
                project.membersIds()
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
                .withMembersIds(new HashSet<>(projectEntity.membersIds))
                .build();
    }

    void updateFrom(Project project) {
        this.title = project.title();
        this.description = project.description();
        this.projectStatus = project.projectStatus();
        this.ownerId = project.ownerId();
        this.createdAt = project.createdAt();
        this.updatedAt = project.updatedAt();
        setMembersIds(project.membersIds());
    }

    private void setMembersIds(Set<Long> membersIds) {
        this.membersIds.clear();
        if (membersIds != null) {
            this.membersIds.addAll(membersIds);
        }
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

}

