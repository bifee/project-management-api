package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.comment.Comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String content,
        Long creatorId
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.id(), comment.content(), comment.creatorId());
    }
}
