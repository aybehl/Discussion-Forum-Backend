package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private String content;
    private Long authorId;
    private Long answerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
