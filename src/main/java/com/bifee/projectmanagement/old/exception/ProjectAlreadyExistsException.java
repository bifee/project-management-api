package com.bifee.projectmanagement.old.exception;

public class ProjectAlreadyExistsException extends RuntimeException {
    public ProjectAlreadyExistsException(String message) {
        super(message);
    }
}
