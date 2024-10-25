package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.QuestionRequestDTO;
import com.forum.discussion_platform.dto.QuestionResponseDTO;
import com.forum.discussion_platform.model.Question;

import java.util.List;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO);
    Question updateQuestion(Long id, Question updatedQuestion);
    Question getQuestion(Long id);
    List<Question> getAllQuestions(int page, int size);
    void deleteQuestion(Long id);
}
