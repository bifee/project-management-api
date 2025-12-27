package com.bifee.projectmanagement.management.infrastructure;

import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;

import java.util.Optional;

class ProjectRepositoryImpl implements ProjectRepository {
    private final JpaProjectRepository jpaProjectRepository;

    public ProjectRepositoryImpl(JpaProjectRepository jpaProjectRepository) {
        this.jpaProjectRepository = jpaProjectRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = ProjectEntity.toEntity(project);
        ProjectEntity savedEntity = jpaProjectRepository.save(entity);
        return ProjectEntity.toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return jpaProjectRepository.findById(id).map(ProjectEntity::toDomain);
    }

    @Override
    public Optional<Project> findByTitle(String title) {
        return jpaProjectRepository.findByTitle(title).map(ProjectEntity::toDomain);
    }

    @Override
    public Optional<Project> findByOwnerId(Long ownerId) {
        return jpaProjectRepository.findByOwnerId(ownerId).map(ProjectEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaProjectRepository.deleteById(id);
    }
}
