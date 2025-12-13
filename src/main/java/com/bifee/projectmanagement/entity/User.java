package com.bifee.projectmanagement.entity;

import java.time.LocalDateTime;

public record User(Long id,
                   Email email,
                   Password password,
                   String name,
                   UserRole role,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt,
                   Boolean isActive) {


    public static final class Builder {
        private Long id;
        private Email email;
        private Password password;
        private String name;
        private UserRole role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isActive;

        public static Builder builder() {
            return new Builder();
        }

        public Builder copy(User source) {
            this.id = source.id;
            this.email = source.email;
            this.password = source.password;
            this.name = source.name;
            this.role = source.role;
            this.createdAt = source.createdAt;
            this.updatedAt = source.updatedAt;
            this.isActive = source.isActive;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withEmail(Email email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(Password password) {
            this.password = password;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public User build() {
            return new User(id, email, password, name, role, createdAt, updatedAt, isActive);
        }

    }
}