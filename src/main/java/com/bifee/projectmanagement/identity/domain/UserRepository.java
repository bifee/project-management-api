package com.bifee.projectmanagement.identity.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(Email email);
    List<User> findAll();
    boolean existsByEmail(Email email);
    void deleteById(Long Id);
}
