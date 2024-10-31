package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.CreateCommentRequestDTO;
import com.forum.discussion_platform.dto.response.CommentResponseDTO;

public interface CommentService {
    CommentResponseDTO createComment(CreateCommentRequestDTO requestDTO, Long authorId);
    CommentResponseDTO editComment(Long commentId, String newBody, Long authorId);
    void deleteComment(Long commentId, Long authorId);
}
