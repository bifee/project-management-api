package com.bifee.projectmanagement.management.domain.project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAllByTitle(String title);
    List<Project> findAllByOwnerId(Long ownerId);
    void deleteById(Long id);

}
