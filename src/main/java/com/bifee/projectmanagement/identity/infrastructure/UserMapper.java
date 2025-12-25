package com.bifee.projectmanagement.identity.infrastructure;


import com.bifee.projectmanagement.identity.domain.User;

class UserMapper {

    public static User toDomain(UserEntity userEntity){
        return new User.Builder()
                .withId(userEntity.getId())
                .withEmail(userEntity.getEmail())
                .withPassword(userEntity.getPassword())
                .withName(userEntity.getName())
                .withRole(userEntity.getRole())
                .withCreatedAt(userEntity.getCreatedAt())
                .withUpdatedAt(userEntity.getUpdatedAt())
                .withActive(userEntity.isActive())
                .build();
    }

    public static UserEntity toEntity(User user){
        return new UserEntity(user.id(), user.name(), user.email(), user.password(), user.role(), user.createdAt(), user.updatedAt(), user.isActive());
    }
}
