package com.bifee.projectmanagement.identity.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @Email
        @Size(max = 254)
        String email,

        @Size(min = 2, max = 50)
        String name
) {
}
