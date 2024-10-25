package com.forum.discussion_platform.dto;

import com.forum.discussion_platform.constants.ApiStatus;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponseDTO {
    private ApiStatus status;
    private int statusCode;
    private ErrorDetails error;

    public ErrorResponseDTO(ApiStatus status, HttpStatus statusCode, String message, String details) {
        this.status = status;
        this.statusCode = statusCode.value();
        this.error = new ErrorDetails(message, details);
    }

    @Data
    public static class ErrorDetails {
        private String message;
        private String details;

        public ErrorDetails(String message, String details) {
            this.message = message;
            this.details = details;
        }
    }
}
