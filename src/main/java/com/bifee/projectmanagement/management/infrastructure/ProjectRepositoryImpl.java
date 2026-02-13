package com.bifee.projectmanagement.management.infrastructure;

import com.bifee.projectmanagement.management.domain.project.Project;
import com.bifee.projectmanagement.management.domain.project.ProjectRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
    public List<Project> findAllByTitle(String title) {
        return jpaProjectRepository.findAllByTitleContaining(title).stream().map(ProjectEntity::toDomain).toList();
    }

    @Override
    public List<Project> findAllByOwnerId(Long ownerId) {
        return jpaProjectRepository.findAllByOwnerId(ownerId).stream().map(ProjectEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaProjectRepository.deleteById(id);
    }
}
