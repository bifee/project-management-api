package com.bifee.profectmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Nome  cannot be null")
    @Size(min = 3, max = 100, message = "Name must have between 3 e 100 characters")
    private String name;

    @NotNull
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;
}
