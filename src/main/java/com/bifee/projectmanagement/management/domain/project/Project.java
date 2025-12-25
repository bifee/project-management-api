package com.bifee.projectmanagement.management.domain.project;

import com.bifee.projectmanagement.management.domain.task.Task;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record Project (Long id,
                       String title,
                       String description,
                       ProjectStatus projectStatus,
                       Long ownerId,
                       Instant createdAt,
                       Instant updatedAt,
                       Set<Long> membersIds,
                       List<Task> tasks
                       ) {

    public Builder mutate() {
        return new Builder().copy(this);
    }

    public static final class Builder{
        private Long id;
        private String title;
        private String description;
        private ProjectStatus projectStatus;
        private Long ownerId;
        private Instant createdAt;
        private Instant updatedAt;
        private Set<Long> membersIds;
        private List<Task> tasks;


        public Builder copy(Project source) {
            this.id = source.id;
            this.title = source.title;
            this.description = source.description;
            this.projectStatus = source.projectStatus;
            this.ownerId = source.ownerId;
            this.createdAt = source.createdAt;
            this.updatedAt = source.updatedAt;
            this.membersIds = source.membersIds;
            this.tasks = source.tasks;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withProjectStatus(ProjectStatus projectStatus) {
            this.projectStatus = projectStatus;
            return this;
        }

        public Builder withOwnerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withMembersIds(Set<Long> membersIds) {
            this.membersIds = membersIds;
            return this;
        }

        public Builder withTasks(List<Task> tasks) {
            this.tasks = tasks;
            return this;
        }

        public Project build() {
            return new Project(id, title, description, projectStatus, ownerId, createdAt, updatedAt, membersIds, tasks);
        }

    }




}
