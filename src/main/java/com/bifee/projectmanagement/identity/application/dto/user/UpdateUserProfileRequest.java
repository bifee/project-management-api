package com.bifee.projectmanagement.identity.application.dto.user;

public record UpdateUserProfileRequest(
        String email,
        String name
) {
}
