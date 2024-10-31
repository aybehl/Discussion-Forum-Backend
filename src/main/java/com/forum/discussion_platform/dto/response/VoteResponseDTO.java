package com.forum.discussion_platform.dto.response;

import com.forum.discussion_platform.enums.VoteType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponseDTO {
    private int upvotes;
    private int downvotes;
    private String userVote;
}
