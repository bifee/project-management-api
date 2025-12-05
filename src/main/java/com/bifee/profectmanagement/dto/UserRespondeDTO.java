package com.bifee.profectmanagement.dto;


import com.bifee.profectmanagement.entity.User;
import com.bifee.profectmanagement.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRespondeDTO {
    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private LocalDateTime createdAt;

    public UserRespondeDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
