package com.bifee.projectmanagement.identity.domain;

import java.time.Instant;


public record User(Long id,
                   Email email,
                   Password password,
                   String name,
                   UserRole role,
                   Instant createdAt,
                   Instant updatedAt,
                   Boolean isActive) {


    public Builder mutate() {
        return new Builder().copy(this);
    }

    public static final class Builder {
        private Long id;
        private Email email;
        private Password password;
        private String name;
        private UserRole role;
        private Instant createdAt;
        private Instant updatedAt;
        private Boolean isActive;

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

        public Builder withCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public User build() {
            validate();
            return new User(id, email, password, name, role, createdAt, updatedAt, isActive);
        }

        private void validate() {
            if (email == null) throw new IllegalArgumentException("Email cannot be null");
            if (password == null) throw new IllegalArgumentException("Password cannot be null");
            if (name == null) throw new IllegalArgumentException("Name cannot be null");
            if (role == null) throw new IllegalArgumentException("Role cannot be null");
            if (createdAt == null) createdAt = Instant.now();
            if (updatedAt == null) updatedAt = Instant.now();
            if (isActive == null) isActive = true;
        }

    }
}