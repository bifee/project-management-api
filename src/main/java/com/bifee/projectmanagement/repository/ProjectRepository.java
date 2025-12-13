package com.bifee.projectmanagement.repository;

import com.bifee.projectmanagement.entity.Project;
import com.bifee.projectmanagement.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<Project> findByName(String name);

    Optional<Project> findByNameLikeIgnoreCase(String name);

    List<Project> findByOwnerId(Long ownerId);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByOwnerIdAndStatus(Long ownerId, ProjectStatus status);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId")
    List<Project> findProjectsByMemberId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Project p WHERE p.owner.id = :userId OR :userId IN (SELECT m.id FROM p.members m)")
    List<Project> findProjectsByOwnerOrMember(@Param("userId") Long userId);
}
