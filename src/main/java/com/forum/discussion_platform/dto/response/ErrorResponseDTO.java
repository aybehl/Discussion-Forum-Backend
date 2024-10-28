package com.forum.discussion_platform.dto.response;

import com.forum.discussion_platform.enums.ApiStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorResponseDTO {
    private ApiStatus status;
    private int statusCode;
    private String message;
}
