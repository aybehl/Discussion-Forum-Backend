package com.forum.discussion_platform.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrEditQuestionResponseDTO {
    private Long questionId;
    private String title;
    private String body;
    private Long authorId;
    private List<String> tags;
    private List<String> mediaUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
