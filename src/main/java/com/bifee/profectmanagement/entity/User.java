package com.bifee.profectmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Password cannot be null")
    @Column(nullable = false)
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @NotNull(message = "Name cannot be null")
    @Column(nullable = false)
    @Size(min = 3, max = 100, message = "Name must have between 3 e 100 characters")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    @Column(nullable = false)
    private UserRole role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "isActive cannot be null")
    @Column(nullable = false)
    private Boolean isActive = true;

}
