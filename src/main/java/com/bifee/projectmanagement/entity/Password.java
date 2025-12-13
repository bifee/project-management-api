package com.bifee.projectmanagement.entity;

public record Password(String value) {
    public Password{
        validate(value);
    }

    public void validate(String password){
        if (password == null || password.isBlank() ) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}
