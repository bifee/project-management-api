package com.bifee.projectmanagement.management.application.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "O conteúdo do comentário não pode estar vazio")
        @Size(max = 2000, message = "Comentário muito longo")
        String content
) {
}
