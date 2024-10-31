package com.forum.discussion_platform.dto.request;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.VoteType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteRequestDTO {
    private Long votedById;
    private Long contentId;          // ID of Question/Answer/Comment
    private ContentType contentType;  // Type of content (QUESTION, ANSWER, COMMENT)
    private VoteType voteType;
}
