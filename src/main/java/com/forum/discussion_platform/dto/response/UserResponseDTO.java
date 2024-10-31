package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long userId;
    private String username;
}
