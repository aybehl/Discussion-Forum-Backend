package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.CreateCommentRequestDTO;
import com.forum.discussion_platform.dto.response.CommentResponseDTO;
import com.forum.discussion_platform.enums.VoteType;

public interface CommentService {
    CommentResponseDTO createComment(CreateCommentRequestDTO requestDTO, Long authorId);
    CommentResponseDTO editComment(Long commentId, String newBody, Long authorId);
    void deleteComment(Long commentId, Long authorId);

    void updateVoteCount(Long contentId, VoteType voteType, int increment);

    String getCommentContent(Long contentId);

    void softDeleteComment(Long contentId, String deletedReason, Long moderatorId);
}
