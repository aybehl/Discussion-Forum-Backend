package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetDetailedQuestionResponseDTO {
    private Long questionId;
    private String title;
    private String body;
    private List<TagResponseDTO> tags;
    private List<MediaResponseDTO> media;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponseDTO author;
    private VoteResponseDTO votes;
    private List<GetDetailedAnswerResponseDTO> answers;
    private boolean isDeleted;
    private String deletedReason;
}
