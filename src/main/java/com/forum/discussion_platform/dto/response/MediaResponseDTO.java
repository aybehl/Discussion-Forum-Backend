package com.forum.discussion_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaResponseDTO {
    private Long mediaId;
    private String mediaUrl;
}
