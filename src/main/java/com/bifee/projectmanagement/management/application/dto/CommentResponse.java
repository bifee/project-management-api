package com.bifee.projectmanagement.management.application.dto;

import com.bifee.projectmanagement.management.domain.comment.Comment;

import java.time.Instant;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        Long creatorId
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.id(), comment.content(), comment.creatorId());
    }

    public static List<CommentResponse> fromList(List<Comment> comments){
        return comments.stream().map(CommentResponse::from).toList();
    }
}
