package com.bifee.projectmanagement.identity.infrastructure;


import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.Password;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRole;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isActive;

    protected UserEntity() {
    }

    public UserEntity(Long id, String name, String email, String password, UserRole role, Instant createdAt, Instant updatedAt, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    protected static User toDomain(UserEntity userEntity){
        return new User.Builder()
                .withId(userEntity.getId())
                .withEmail(new Email(userEntity.getEmail()))
                .withPassword(new Password(userEntity.getPassword()))
                .withName(userEntity.getName())
                .withRole(userEntity.getRole())
                .withCreatedAt(userEntity.getCreatedAt())
                .withUpdatedAt(userEntity.getUpdatedAt())
                .withActive(userEntity.isActive())
                .build();
    }

    protected static UserEntity toEntity(User user){
        return new UserEntity(user.id(), user.name(), user.email().value(), user.password().value(), user.role(), user.createdAt(), user.updatedAt(), user.isActive());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }
}
