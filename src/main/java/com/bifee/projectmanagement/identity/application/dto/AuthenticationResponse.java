package com.bifee.projectmanagement.identity.application.dto;

public record AuthenticationResponse(
        String token,
        String tokenType
        )
{

}
