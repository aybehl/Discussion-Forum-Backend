package com.forum.discussion_platform.exception;

public class ContentAlreadyModeratedException extends RuntimeException{
    public ContentAlreadyModeratedException(String message) {
        super(message);
    }
}
