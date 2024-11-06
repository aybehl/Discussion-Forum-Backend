package com.forum.discussion_platform.dto.request;

import lombok.Data;

@Data
public class CreateCommentRequestDTO {
    private Long answerId;
    private String body;
}
