package com.bifee.projectmanagement.entity;

public record Password(String value) {

    public Password {
        validate(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank() ) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (value.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}
