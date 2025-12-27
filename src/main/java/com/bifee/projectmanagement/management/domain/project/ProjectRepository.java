package com.bifee.projectmanagement.management.domain.project;

import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    Optional<Project> findByTitle(String title);
    Optional<Project> findByOwnerId(Long ownerId);
    void deleteById(Long id);

}
