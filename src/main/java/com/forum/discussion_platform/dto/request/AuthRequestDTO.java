package com.forum.discussion_platform.dto.request;

import com.forum.discussion_platform.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDTO {
    private String email;
    private String password; // For email/password sign-up only
    private String googleToken; // For Google sign-up only
    private UserRole role;
}
