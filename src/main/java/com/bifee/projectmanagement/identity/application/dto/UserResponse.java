package com.bifee.projectmanagement.identity.application.dto;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.Password;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRole;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String name,
        UserRole role,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.id(),
                user.email().value(),
                user.name(),
                user.role(),
                user.isActive(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public static List<UserResponse> fromList(List<User> users){
        return users.stream().map(UserResponse::from).toList();
    }
}
