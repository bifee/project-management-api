package com.bifee.projectmanagement.management.domain.comment;

import com.bifee.projectmanagement.management.domain.task.Task;

import java.time.Instant;

public record Comment (
     Long id,
     String content,
     Long creatorId,
     Instant createdAt,
     Instant updatedAt
){
    public Builder mutate() {
        return new Builder().copy(this);
    }

    public static final class Builder{
        private Long id;
        private String content;
        private Long creatorId;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder copy(Comment comment){
            this.id = comment.id();
            this.content = comment.content();
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withCreator(Long creatorId) {
            this.creatorId = creatorId;
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

        public Comment build(){
            validate();
            return new Comment(id, content, creatorId, createdAt, updatedAt);
        }

        private void validate() {
            if (content == null) throw new IllegalArgumentException("Content cannot be null");
            if (creatorId == null) throw new IllegalArgumentException("Creator cannot be null");
            if (createdAt == null) createdAt = Instant.now();
            if (updatedAt == null) updatedAt = Instant.now();
        }
    }
}
