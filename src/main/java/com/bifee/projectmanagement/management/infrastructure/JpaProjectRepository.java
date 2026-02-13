package com.bifee.projectmanagement.management.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findAllByTitleContaining(String title);
    List<ProjectEntity> findAllByOwnerId(Long ownerId);
}
