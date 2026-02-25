package com.bifee.projectmanagement.shared;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> validationErrors,
        String path
) {


    public static ValidationErrorResponse of(
            int status,
            String error,
            String message,
            Map<String, String> validationErrors,
            String path
    ) {
        return new ValidationErrorResponse(
                Instant.now(),
                status,
                error,
                message,
                validationErrors,
                path
        );
    }
}
