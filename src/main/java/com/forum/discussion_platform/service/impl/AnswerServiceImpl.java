package com.forum.discussion_platform.service.impl;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.AnswerRequestDTO;
import com.forum.discussion_platform.dto.response.AnswerResponseDTO;
import com.forum.discussion_platform.enums.ContentStatus;
import com.forum.discussion_platform.enums.VoteType;
import com.forum.discussion_platform.exception.ContentAlreadyDeleted;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import com.forum.discussion_platform.model.Answer;
import com.forum.discussion_platform.model.Question;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.AnswerRepository;
import com.forum.discussion_platform.repository.QuestionRepository;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.service.AnswerService;
import com.forum.discussion_platform.util.DTOMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository, UserRepository userRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AnswerResponseDTO createAnswer(AnswerRequestDTO requestDTO, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        Question question = questionRepository.findById(requestDTO.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.QUESTION_NOT_FOUND));

        if (requestDTO.getBody() != null && requestDTO.getBody().length() > 0) {
            Answer answer = new Answer();
            answer.setBody(requestDTO.getBody());
            answer.setAnsweredBy(author);
            answer.setRelatedQuestion(question);

            Answer savedAnswer = answerRepository.save(answer);
            return DTOMapper.mapToAnswerResponseDTO(savedAnswer);
        } else {
            throw new IllegalArgumentException(GenericConstants.INVALID_CONTENT_FOR_ANSWER);
        }
    }

    @Override
    public AnswerResponseDTO editAnswer(Long answerId, String newBody, Long authorId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.ANSWER_NOT_FOUND));

        if (!answer.getAnsweredBy().getUserId().equals(authorId)) {
            throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_ANSWER_UPDATE);
        }

        if(answer.isDeleted()){
            throw new ContentAlreadyDeleted(GenericConstants.ANSWER_ALREADY_DELETED);
        }

        if (newBody != null && newBody.length() > 0) {
            answer.setBody(newBody);
        } else {
            throw new IllegalArgumentException(GenericConstants.INVALID_CONTENT_FOR_ANSWER);
        }

        Answer updatedAnswer = answerRepository.save(answer);
        return DTOMapper.mapToAnswerResponseDTO(updatedAnswer);
    }

    @Override
    public void deleteAnswer(Long answerId, Long authorId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.ANSWER_NOT_FOUND));

        if (!answer.getAnsweredBy().getUserId().equals(authorId)) {
            throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_ANSWER_DELETE);
        }

        if(answer.isDeleted()){
            throw new ContentAlreadyDeleted(GenericConstants.ANSWER_ALREADY_DELETED);
        }

        answer.setDeleted(true);
        answer.setDeletedAt(LocalDateTime.now());
        answer.setDeletedBy(authorId);
        answer.setContentStatus(ContentStatus.DELETED);
        answer.setDeletedReason(GenericConstants.DELETED_BY_AUTHOR);
        answerRepository.save(answer);
    }

    public void updateVoteCount(Long answerId, VoteType voteType, int increment) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException(GenericConstants.ANSWER_NOT_FOUND));

        if (voteType == VoteType.UPVOTE) {
            answer.setUpvotes(answer.getUpvotes() + increment);
        } else {
            answer.setDownvotes(answer.getDownvotes() + increment);
        }

        answerRepository.save(answer);
    }
}
