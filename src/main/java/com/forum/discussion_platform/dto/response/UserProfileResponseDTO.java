package com.forum.discussion_platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponseDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String profilePic;
    private LocalDateTime joinedAt;
}
