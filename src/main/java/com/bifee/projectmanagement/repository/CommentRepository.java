package com.bifee.projectmanagement.repository;

import com.bifee.projectmanagement.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<List<Comment>> findByContentLikeIgnoreCase(String content);

    List<Comment> findByTaskId(Long taskId);

    List<Comment> findByCreatorId(Long userId);

    List<Comment> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);

    Long countCommentsByTask(@Param("taskId") Long taskId);
}
