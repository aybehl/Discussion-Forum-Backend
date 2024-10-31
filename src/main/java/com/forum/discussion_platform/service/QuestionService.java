package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.dto.request.QuestionRequestDTO;
import com.forum.discussion_platform.dto.response.CreateOrEditQuestionResponseDTO;
import com.forum.discussion_platform.dto.response.GetDetailedQuestionResponseDTO;
import com.forum.discussion_platform.dto.response.GetQuestionResponseDTO;
import com.forum.discussion_platform.enums.VoteType;
import com.forum.discussion_platform.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    CreateOrEditQuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO, List<MultipartFile> mediaFiles, Long authorId);
    CreateOrEditQuestionResponseDTO updateQuestion(Long id, EditQuestionRequestDTO requestDTO, List<MultipartFile> mediaFiles, Long authorId);

    Page<GetQuestionResponseDTO> getAllQuestionsWithPagination(int page, int size);

    void deleteQuestion(Long id, Long authorId);

    Page<GetQuestionResponseDTO> getAllQuestionsByTags(List<Long> tagsIds, int page, int size);

    GetDetailedQuestionResponseDTO getQuestionById(Long questionId, Long userId);

    void updateVoteCount(Long contentId, VoteType voteType, int increment);
}
