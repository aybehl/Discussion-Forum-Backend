package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetDetailedCommentResponseDTO {
    private Long commentId;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponseDTO author;
    private VoteResponseDTO votes;
    private boolean isDeleted;
    private String deletedReason;
}
