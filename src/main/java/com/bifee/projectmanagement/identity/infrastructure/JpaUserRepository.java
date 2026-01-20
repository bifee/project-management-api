package com.bifee.projectmanagement.identity.infrastructure;

import com.bifee.projectmanagement.identity.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailValue(String emailValue);
    Optional<UserEntity> findByEmail(String value);
}
