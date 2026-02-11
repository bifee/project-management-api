package com.bifee.projectmanagement.management.domain.task;

import com.bifee.projectmanagement.management.domain.comment.Comment;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public record Task (
     Long id,
     String title,
     String description,
     TaskStatus status,
     TaskPriority priority,
     Set<Long> assignedUsersId,
     Long projectId,
     Instant createdAt,
     Instant updatedAt,
     List<Comment> comments
){

    public Comment getCommentById(Long commentId) {
        return comments.stream().
                filter(comment -> comment.id().equals(commentId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Comment not found: " + commentId));
    }

    public Builder mutate() {
        return new Builder().copy(this);
    }

    public static final class Builder{

        private Long id;
        private String title;
        private String description;
        private TaskStatus status;
        private TaskPriority priority;
        private Set<Long> assignedUsersId;
        private Long projectId;
        private Instant createdAt;
        private Instant updatedAt;
        private List<Comment> comments;

        public Builder copy(Task source) {
            this.id = source.id;
            this.title = source.title;
            this.description = source.description;
            this.status = source.status;
            this.priority = source.priority;
            this.assignedUsersId = source.assignedUsersId;
            this.projectId = source.projectId;
            this.createdAt = source.createdAt;
            this.updatedAt = source.updatedAt;
            this.comments = source.comments;
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

        public Builder withStatus(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Builder withPriority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder withAssignedUser(Set<Long> assignedUsersIds) {
            this.assignedUsersId = assignedUsersIds;
            return this;
        }

        public Builder withProject(Long projectId) {
            this.projectId = projectId;
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

        public Builder withComments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public Task build() {
            validate();
            return new Task(id, title, description, status, priority, assignedUsersId, projectId, createdAt, updatedAt, comments);
        }

        private void validate() {
            if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be null or blank");
            if (projectId == null) throw new IllegalArgumentException("Project cannot be null");
            if (status == null){
                status = TaskStatus.TO_DO;
            }
            if (priority == null){
                priority = TaskPriority.LOW;
            }
            if (createdAt == null) createdAt = Instant.now();
            if (updatedAt == null) updatedAt = Instant.now();
        }

    }
}

