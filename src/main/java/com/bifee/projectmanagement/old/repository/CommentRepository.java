package com.bifee.projectmanagement.old.repository;

import com.bifee.projectmanagement.management.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findByContentLikeIgnoreCase(String content);

    List<Comment> findByTaskId(Long taskId);

    List<Comment> findByCreatorId(Long userId);

    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId ORDER BY c. createdAt DESC")
    List<Comment> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c. task.id = :taskId")
    Long countCommentsByTask(@Param("taskId") Long taskId);
}
