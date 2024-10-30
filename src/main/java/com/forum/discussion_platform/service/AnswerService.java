package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.AnswerRequestDTO;
import com.forum.discussion_platform.dto.response.AnswerResponseDTO;

public interface AnswerService {
    AnswerResponseDTO createAnswer(AnswerRequestDTO requestDTO, Long authorId);
    AnswerResponseDTO editAnswer(Long answerId, String newBody, Long authorId);
    void deleteAnswer(Long answerId, Long authorId);
}
