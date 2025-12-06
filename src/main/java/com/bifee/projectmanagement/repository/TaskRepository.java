package com.bifee.projectmanagement.repository;

import com.bifee.projectmanagement.entity.Task;
import com.bifee.projectmanagement.entity.TaskPriority;
import com.bifee.projectmanagement.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTitleLikeIgnoreCase(String pattern);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignedUserId(Long userId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority);

    List<Task> findByAssignedUserIdAndStatus(Long userId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findByProjectIdOrderedByPriority(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    Long countTasksByProjectAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
}
