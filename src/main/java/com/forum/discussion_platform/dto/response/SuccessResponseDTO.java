package com.forum.discussion_platform.dto;

import com.forum.discussion_platform.constants.ApiStatus;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class SuccessResponseDTO<T> {
    private ApiStatus status;
    private T data;
    private int statusCode;
    private String message;

    public SuccessResponseDTO(ApiStatus status, T data, HttpStatus statusCode, String message) {
        this.status = status;
        this.data = data;
        this.statusCode = statusCode.value();
        this.message = message;
    }
}
