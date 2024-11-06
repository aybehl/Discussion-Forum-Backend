package com.forum.discussion_platform.dto.request;

import com.forum.discussion_platform.enums.ReportStatus;
import lombok.Data;

@Data
public class ModerationRequestDTO {
    private ReportStatus action;
    private String deletedReason;
}
