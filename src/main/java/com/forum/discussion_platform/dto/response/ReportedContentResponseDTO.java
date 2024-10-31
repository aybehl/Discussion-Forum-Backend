package com.forum.discussion_platform.dto.response;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.ReportReason;
import com.forum.discussion_platform.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportedContentResponseDTO {
    private Long reportId;
    private ContentType contentType;
    private Long contentId;
    private Long reportedById;
    private ReportReason reason;
    private String additionalComment;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
