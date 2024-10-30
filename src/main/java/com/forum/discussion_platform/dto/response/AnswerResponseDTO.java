package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnswerResponseDTO {
    private Long answerId;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long authorId;
    private Long questionId;
}
