package com.bifee.projectmanagement.management.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;


@Repository
interface JpaTaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByProjectId(Long projectId);
}
