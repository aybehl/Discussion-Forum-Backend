package com.forum.discussion_platform.service;

import com.forum.discussion_platform.enums.UserRole;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    public Long getUserIdFromToken(String token) {
        return 1L;
    }

    public UserRole getUserRoleFromToken(String token){
        return UserRole.USER;
    }
}
