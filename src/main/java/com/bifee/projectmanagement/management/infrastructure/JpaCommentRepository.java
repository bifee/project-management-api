package com.bifee.projectmanagement.management.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface JpaCommentRepository extends JpaRepository<CommentEntity, Long> {
    Optional<CommentEntity> findByTaskId(Long taskId);
}
