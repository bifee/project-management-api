package com.bifee.profectmanagement.exception;

public class UserAlreadyExistsEsception extends RuntimeException {
    public UserAlreadyExistsEsception(String message) {
        super(message);
    }
}
