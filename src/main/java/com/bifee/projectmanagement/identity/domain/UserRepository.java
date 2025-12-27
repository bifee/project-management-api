package com.bifee.projectmanagement.identity.domain;

import java.util.List;

public interface UserRepository {
    User save(User user);
    <Optional>User findById(Long id);
    List<User> findAll();
    <Optional>User existsByEmail(Email email);
    void deleteById(Long Id);
}
