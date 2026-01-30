package com.bifee.projectmanagement.management.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;


@Repository
public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByTitle(String title);

    Optional<ProjectEntity> findByOwnerId(Long ownerId);
}
