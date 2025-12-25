package com.bifee.projectmanagement.identity.domain;

import java.util.regex.Pattern;

public record Email(String value) {
    public Email {
        validate(value);
    }

    public void validate(String email){
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$";
        if(email == null || !Pattern.matches(emailRegex, email)){
            throw new IllegalArgumentException("Email Invalid");
        }
    }
}
