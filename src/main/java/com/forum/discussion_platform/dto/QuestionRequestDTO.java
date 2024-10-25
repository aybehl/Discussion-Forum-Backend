package com.forum.discussion_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuestionRequestDTO {
    private String title;
    private String body;
    private Long authorId;
    private List<Long> tagIds;
    private List<String> mediaUrls;
}
