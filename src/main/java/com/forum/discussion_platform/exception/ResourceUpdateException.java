package com.forum.discussion_platform.exception;

import com.forum.discussion_platform.enums.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceUpdateException extends RuntimeException {
    private final ContentType resourceName;
    private final Long resourceId;

    public ResourceUpdateException(ContentType resourceName, Long resourceId, String message) {
        super(String.format("%s with ID %d could not be updated: %s", resourceName, resourceId, message));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public ContentType getResourceName() {
        return resourceName;
    }

    public Long getResourceId() {
        return resourceId;
    }
}
