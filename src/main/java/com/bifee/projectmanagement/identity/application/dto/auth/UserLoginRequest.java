package com.bifee.projectmanagement.identity.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank String email,
        @NotBlank String password
) { }
