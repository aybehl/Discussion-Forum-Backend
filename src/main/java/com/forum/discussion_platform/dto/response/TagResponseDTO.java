package com.forum.discussion_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagResponseDTO {
    private Long tagId;
    private String tagName;
    private String description;
}
