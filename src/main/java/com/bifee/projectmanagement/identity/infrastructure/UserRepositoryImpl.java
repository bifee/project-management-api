package com.bifee.projectmanagement.identity.infrastructure;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;

import java.util.List;

class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaProjectRepository;
    public UserRepositoryImpl(JpaUserRepository jpaProjectRepository) {
        this.jpaProjectRepository = jpaProjectRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.toEntity(user);
        UserEntity savedEntity = jpaProjectRepository.save(entity);
        return UserEntity.toDomain(savedEntity);
    }

    @Override
    public <Optional> User existsByEmail(Email email) {
        return jpaProjectRepository.findByEmailValue(email.value()).map(UserEntity::toDomain).orElse(null);
    }

    @Override
    public <Optional> User findById(Long id) {
        return jpaProjectRepository.findById(id).map(UserEntity::toDomain).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return jpaProjectRepository.findAll().stream().map(UserEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaProjectRepository.deleteById(id);
    }
}
