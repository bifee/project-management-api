package com.bifee.projectmanagement.management.domain.comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(Long id);
    List<Comment> findByTaskId(Long taskId);
    void deleteById(Long id);
}
