package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.dto.request.QuestionRequestDTO;
import com.forum.discussion_platform.dto.response.QuestionResponseDTO;
import com.forum.discussion_platform.model.Question;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO, List<MultipartFile> mediaFiles, Long authorId);
    QuestionResponseDTO updateQuestion(Long id, EditQuestionRequestDTO requestDTO, List<MultipartFile> mediaFiles, Long authorId);
    Question getQuestion(Long id);
    List<Question> getAllQuestions(int page, int size);
    void deleteQuestion(Long id);
}
