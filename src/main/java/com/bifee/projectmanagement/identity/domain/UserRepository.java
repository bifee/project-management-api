package com.bifee.projectmanagement.identity.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(Email email);
    List<User> findAll();
    List<User> findByIsActiveTrue();
    List<User> findByRole(UserRole role);
    List<User> findByName(String name);
    boolean existsByEmail(String email);
    void deleteById(Long Id);
}
