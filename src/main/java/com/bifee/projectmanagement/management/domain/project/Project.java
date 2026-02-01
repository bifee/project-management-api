package com.bifee.projectmanagement.management.domain.project;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public record Project (Long id,
                       String title,
                       String description,
                       ProjectStatus projectStatus,
                       Long ownerId,
                       Instant createdAt,
                       Instant updatedAt,
                       Set<Long> membersIds
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


        public Builder copy(Project source) {
            this.id = source.id;
            this.title = source.title;
            this.description = source.description;
            this.projectStatus = source.projectStatus;
            this.ownerId = source.ownerId;
            this.createdAt = source.createdAt;
            this.updatedAt = source.updatedAt;
            this.membersIds = source.membersIds;
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

        public Project build() {
            validate();
            return new Project(id, title, description, projectStatus, ownerId, createdAt, updatedAt, membersIds);
        }

        private void validate() {
            if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be null or blank");
            if (ownerId == null) throw new IllegalArgumentException("Owner cannot be null");
            if (projectStatus == null) projectStatus = ProjectStatus.IN_PROGRESS;
            if (membersIds == null || membersIds.isEmpty()) membersIds = Set.of(ownerId);
            else if (!membersIds.contains(ownerId)) {
                Set<Long> updatedMembers = new HashSet<>(membersIds);
                updatedMembers.add(ownerId);
                membersIds = updatedMembers;
            }
            if (createdAt == null) createdAt = Instant.now();
            if (updatedAt == null) updatedAt = Instant.now();
        }

    }




}
