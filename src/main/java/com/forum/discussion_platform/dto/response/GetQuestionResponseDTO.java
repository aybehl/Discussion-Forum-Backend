package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetQuestionResponseDTO {
    private Long questionId;
    private String title;
    private List<TagResponseDTO> tags;
    private LocalDateTime createdAt;
    private int upvotes;
    private int downvotes;
    private int noOfReplies;
}
