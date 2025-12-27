package com.bifee.projectmanagement.management.infrastructure;

import com.bifee.projectmanagement.management.domain.comment.Comment;
import com.bifee.projectmanagement.management.domain.comment.CommentRepository;

import java.util.List;
import java.util.Optional;

class CommentRepositoryImpl implements CommentRepository {
    private final JpaCommentRepository jpaCommentRepository;

    public CommentRepositoryImpl(JpaCommentRepository jpaCommentRepository) {
        this.jpaCommentRepository = jpaCommentRepository;
    }

    @Override
    public Comment save(Comment comment) {
        CommentEntity entity = CommentEntity.toEntity(comment);
        CommentEntity savedEntity = jpaCommentRepository.save(entity);
        return CommentEntity.toDomain(savedEntity);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return jpaCommentRepository.findById(id).map(CommentEntity::toDomain);
    }

    @Override
    public List<Comment> findByTaskId(Long taskId) {
        return jpaCommentRepository.findByTaskId(taskId).stream().map(CommentEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaCommentRepository.deleteById(id);
    }
}
