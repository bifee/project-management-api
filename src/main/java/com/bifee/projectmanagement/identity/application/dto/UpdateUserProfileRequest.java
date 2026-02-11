package com.bifee.projectmanagement.identity.application.dto;

import com.bifee.projectmanagement.identity.domain.Email;

public record UpdateUserProfileRequest(
        String email,
        String name
) {
}
