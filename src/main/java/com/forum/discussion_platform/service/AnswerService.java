package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.AnswerRequestDTO;
import com.forum.discussion_platform.dto.response.AnswerResponseDTO;
import com.forum.discussion_platform.enums.VoteType;

public interface AnswerService {
    AnswerResponseDTO createAnswer(AnswerRequestDTO requestDTO, Long authorId);
    AnswerResponseDTO editAnswer(Long answerId, String newBody, Long authorId);
    void deleteAnswer(Long answerId, Long authorId);

    void updateVoteCount(Long contentId, VoteType voteType, int increment);

    String getAnswerContent(Long contentId);

    void softDeleteAnswer(Long contentId, String deletedReason, Long moderatorId);
}
