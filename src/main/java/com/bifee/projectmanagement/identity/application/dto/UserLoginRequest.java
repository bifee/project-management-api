package com.bifee.projectmanagement.identity.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
        @NotBlank String email,
        @NotBlank String password
) { }
