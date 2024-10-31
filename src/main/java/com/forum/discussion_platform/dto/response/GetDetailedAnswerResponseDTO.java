package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetDetailedAnswerResponseDTO {
    private Long answerId;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponseDTO author;
    private VoteResponseDTO votes;
    private List<GetDetailedCommentResponseDTO> comments;
    private boolean isDeleted;
    private String deletedReason;
}
