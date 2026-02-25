package com.bifee.projectmanagement.identity.application.dto.auth;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.Password;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record UserRegistrationRequest(
        @NotBlank String name,

        @jakarta.validation.constraints.Email
        @NotBlank
        @Size(max = 254)
        String email,
        @NotBlank @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
                message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
        )
        String password,
        @NotNull
        UserRole role
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
