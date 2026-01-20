package com.bifee.projectmanagement.identity.application.dto;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.Password;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotNull UserRole role
) {

    public User toDomain(String encondedPassword) {
        return new User.Builder()
                .withName(this.name())
                .withEmail(new Email(this.email()))
                .withPassword(new Password(encondedPassword))
                .withRole(this.role)
                .build();
    }
}
