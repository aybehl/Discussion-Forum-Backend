package com.forum.discussion_platform.dto.request;

import lombok.Data;

@Data
public class AnswerRequestDTO {
    private String body;
    private Long questionId;
}
