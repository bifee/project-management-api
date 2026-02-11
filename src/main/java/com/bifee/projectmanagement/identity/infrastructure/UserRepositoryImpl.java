package com.bifee.projectmanagement.identity.infrastructure;

import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import com.bifee.projectmanagement.identity.domain.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    public UserRepositoryImpl(JpaUserRepository jpaProjectRepository) {
        this.jpaUserRepository = jpaProjectRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return UserEntity.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaUserRepository.findByEmail(email.value()).map(UserEntity::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream().map(UserEntity::toDomain).toList();
    }

    @Override
    public List<User> findByIsActiveTrue(){
        return jpaUserRepository.findByIsActiveTrue().stream().map(UserEntity::toDomain).toList();
    }

    @Override
    public List<User> findByRole(UserRole role){
        return jpaUserRepository.findByRole(role).stream().map(UserEntity::toDomain).toList();
    }

    @Override
    public List<User> findByName(String name){
        return jpaUserRepository.findByNameContaining(name).stream().map(UserEntity::toDomain).toList();
    }
    @Override
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }
}
