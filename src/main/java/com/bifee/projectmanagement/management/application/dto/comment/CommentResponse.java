package com.bifee.projectmanagement.management.application.dto.comment;

import com.bifee.projectmanagement.management.domain.comment.Comment;

import java.time.Instant;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        Long creatorId,
        Instant createdAt,
        Instant updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.id(), comment.content(), comment.creatorId(), comment.createdAt(), comment.updatedAt());
    }

    public static List<CommentResponse> fromList(List<Comment> comments){
        return comments.stream().map(CommentResponse::from).toList();
    }
}
