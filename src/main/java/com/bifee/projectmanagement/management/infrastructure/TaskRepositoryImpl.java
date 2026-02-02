package com.bifee.projectmanagement.management.infrastructure;

import com.bifee.projectmanagement.management.domain.task.Task;
import com.bifee.projectmanagement.management.domain.task.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
class TaskRepositoryImpl implements TaskRepository {
    private final JpaTaskRepository jpaTaskRepository;

    public TaskRepositoryImpl(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = TaskEntity.toEntity(task);
        TaskEntity savedEntity = jpaTaskRepository.save(entity);
        return TaskEntity.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpaTaskRepository.findById(id).map(TaskEntity::toDomain);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return jpaTaskRepository.findByProjectId(projectId).stream().map(TaskEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaTaskRepository.deleteById(id);
    }
}
