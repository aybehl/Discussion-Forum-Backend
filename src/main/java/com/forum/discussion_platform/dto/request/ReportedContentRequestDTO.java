package com.forum.discussion_platform.dto.request;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.ReportReason;
import lombok.Data;

@Data
public class ReportedContentRequestDTO {
    private ContentType contentType;
    private Long contentId;
    private ReportReason reason;
    private String additionalComment;
}
