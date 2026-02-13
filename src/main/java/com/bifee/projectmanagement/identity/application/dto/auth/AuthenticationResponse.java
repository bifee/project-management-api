package com.bifee.projectmanagement.identity.application.dto.auth;

public record AuthenticationResponse(
        String token,
        String tokenType,
        String username
        )
{}
