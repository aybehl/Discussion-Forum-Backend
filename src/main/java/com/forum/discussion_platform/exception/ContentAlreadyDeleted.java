package com.forum.discussion_platform.exception;

public class ContentAlreadyDeleted extends IllegalArgumentException{
    public ContentAlreadyDeleted(String s) {
        super(s);
    }
}
