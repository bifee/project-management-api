package com.bifee.projectmanagement.shared;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Passwords does not match");
    }
    public PasswordMismatchException(String message) {
        super(message);
    }
}
