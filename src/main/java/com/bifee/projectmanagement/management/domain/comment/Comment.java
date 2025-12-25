package com.bifee.projectmanagement.management.domain.comment;

import java.time.Instant;

public record Comment (
     Long id,
     String content,
     Long creatorId,
     Long taskId,
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
        private Long taskId;
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

        public Builder withTask(Long taskId) {
            this.taskId = taskId;
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
            return new Comment(id, content, creatorId, taskId, createdAt, updatedAt);
        }
    }
}
