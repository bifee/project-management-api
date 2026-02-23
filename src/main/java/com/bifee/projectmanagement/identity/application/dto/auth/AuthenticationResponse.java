package com.bifee.projectmanagement.identity.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationResponse(
        @NotBlank
        String token,
        @NotBlank
        String tokenType,
        @NotBlank
        String username
        )
{}
