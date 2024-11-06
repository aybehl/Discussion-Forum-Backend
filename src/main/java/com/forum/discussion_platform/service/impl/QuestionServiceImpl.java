package com.forum.discussion_platform.service.impl;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.dto.request.QuestionRequestDTO;
import com.forum.discussion_platform.dto.response.*;
import com.forum.discussion_platform.enums.ContentStatus;
import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.VoteType;
import com.forum.discussion_platform.exception.ContentAlreadyDeleted;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.exception.ResourceUpdateException;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import com.forum.discussion_platform.model.Media;
import com.forum.discussion_platform.model.Question;
import com.forum.discussion_platform.model.Tag;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.QuestionRepository;
import com.forum.discussion_platform.repository.TagRepository;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.service.MediaService;
import com.forum.discussion_platform.service.QuestionService;
import com.forum.discussion_platform.service.TagService;
import com.forum.discussion_platform.service.VoteService;
import com.forum.discussion_platform.util.DTOMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final MediaService mediaService;
    private final TagService tagService;

    private final VoteService voteService;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               UserRepository userRepository,
                               TagRepository tagRepository,
                               MediaService mediaService,
                               TagService tagService, VoteService voteService){
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.mediaService = mediaService;
        this.tagService = tagService;
        this.voteService = voteService;
    }

    @Override
    @Transactional
    public CreateOrEditQuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO, List<MultipartFile> mediaFiles, Long authorId) {
        try {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new UnauthorizedAccessException(GenericConstants.USER_NOT_FOUND));

            if(requestDTO.getTitle() == null || requestDTO.getBody() == null){
                throw new IllegalArgumentException(GenericConstants.INVALID_CONTENT_FOR_QUESTION);
            }

            List<Tag> tags = tagRepository.findAllById(requestDTO.getTagIds());

            Question question = new Question();
            question.setTitle(requestDTO.getTitle());
            question.setBody(requestDTO.getBody());
            question.setAuthor(author);
            question.setTags(tags);

            Question savedQuestion = questionRepository.save(question);

            List<Media> mediaList = null;
            if(mediaFiles != null && mediaFiles.size() > 0){
                mediaList = mediaService.processAndSaveMediaFiles(mediaFiles, savedQuestion.getQuestionId(), ContentType.QUESTION);
            }

            return DTOMapper.mapToQuestionResponseDTOWithMedia(savedQuestion, mediaList);
        } catch(Exception ex){
            // Rollback transaction if media upload fails
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Transactional
    public CreateOrEditQuestionResponseDTO updateQuestion(Long questionId, EditQuestionRequestDTO requestDTO, List<MultipartFile> newMediaFiles, Long authorId) {
        try {
            //Validate the question
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.QUESTION_NOT_FOUND));

            // Validate the author
            if (!question.getAuthor().getUserId().equals(authorId)) {
                throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_QUESTION_UPDATE);
            }

            if(requestDTO.getTitle() != null){
                question.setTitle(requestDTO.getTitle());
            }
            if(requestDTO.getBody() != null){
                question.setBody(requestDTO.getBody());
            }

            List<Tag> updatedTags = new ArrayList<>(tagService.manageTagsForQuestion(
                    requestDTO.getNewTagIds(),
                    requestDTO.getTagsToDelete(),
                    question.getTags()
            ));

            if(tagService.checkIfTagsUpdated(question.getTags(), updatedTags)){
                //Clear existing tag associations
                question.getTags().clear();
                questionRepository.save(question);

                //Add new tag associations
                question.setTags(updatedTags);
            }

            Question savedQuestion = questionRepository.save(question);

            List<Media> updatedMedia = mediaService.manageMedia(requestDTO.getMediaToDelete(), newMediaFiles, question.getQuestionId(), ContentType.QUESTION);
            return DTOMapper.mapToQuestionResponseDTOWithMedia(savedQuestion, updatedMedia);
        } catch(Exception ex){
            // Rollback transaction if media upload fails
            throw new ResourceUpdateException(ContentType.QUESTION, questionId, "Failed to update due to an internal error.");
        }
    }

    @Override
    public Page<GetQuestionResponseDTO> getAllQuestionsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> questionData = questionRepository.findQuestionsWithAnswerCount(pageable);

        return questionData.map(data -> {
            Long questionId = (Long) data[0];
            String title = (String) data[1];
            LocalDateTime createdAt = (LocalDateTime) data[2];
            int upvotes = (int) data[3];
            int downvotes = (int) data[4];
            int noOfReplies = ((Number) data[5]).intValue();

            List<TagResponseDTO> tags = tagRepository.findAllTagsForQuestion(questionId)
                    .stream()
                    .map(tag -> new TagResponseDTO(tag.getTagId(), tag.getName(), tag.getDescription()))
                    .collect(Collectors.toList());

            return GetQuestionResponseDTO.builder()
                    .questionId(questionId)
                    .title(title)
                    .tags(tags)
                    .upvotes(upvotes)
                    .downvotes(downvotes)
                    .noOfReplies(noOfReplies)
                    .createdAt(createdAt)
                    .build();
        });
    }

    @Override
    public Page<GetQuestionResponseDTO> getAllQuestionsByTags(List<Long> tagIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> questionData = questionRepository.findQuestionsWithAnswerCountByTags(tagIds, pageable);

        return questionData.map(data -> {
            Long questionId = (Long) data[0];
            String title = (String) data[1];
            LocalDateTime createdAt = (LocalDateTime) data[2];
            int upvotes = (int) data[3];
            int downvotes = (int) data[4];
            int noOfReplies = ((Number) data[5]).intValue();

            List<TagResponseDTO> tags = tagRepository.findAllTagsForQuestion(questionId)
                    .stream()
                    .map(tag -> new TagResponseDTO(tag.getTagId(), tag.getName(), tag.getDescription()))
                    .collect(Collectors.toList());

            return GetQuestionResponseDTO.builder()
                    .questionId(questionId)
                    .title(title)
                    .tags(tags)
                    .upvotes(upvotes)
                    .downvotes(downvotes)
                    .noOfReplies(noOfReplies)
                    .createdAt(createdAt)
                    .build();
        });
    }

    @Override
    public void deleteQuestion(Long questionId, Long authorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.QUESTION_NOT_FOUND));

        if (!question.getAuthor().getUserId().equals(authorId)) {
            throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_QUESTION_DELETE);
        }

        if(question.isDeleted()){
            throw new ContentAlreadyDeleted(GenericConstants.QUESTION_ALREADY_DELETED);
        }

        question.setDeleted(true);
        question.setDeletedAt(LocalDateTime.now());
        question.setDeletedBy(authorId);
        question.setContentStatus(ContentStatus.DELETED);
        question.setDeletedReason(GenericConstants.DELETED_BY_AUTHOR);

        questionRepository.save(question);
    }

    @Override
    @Transactional(readOnly = true)
    public GetDetailedQuestionResponseDTO getQuestionById(Long questionId, Long userId) {
        //Validate the question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.QUESTION_NOT_FOUND));

        // Get vote type for the question
        String questionUserVote = voteService.getUserVoteType(question.getQuestionId(), userId, ContentType.QUESTION);

        Optional<List<Media>> mediaList = mediaService.findByContentIdAndType(questionId, ContentType.QUESTION);

        // Prepare answer DTOs with votes and comments
        List<GetDetailedAnswerResponseDTO> answerResponseDTOs = question.getAnswers().stream().map(answer -> {
            String answerUserVote = voteService.getUserVoteType(answer.getAnswerId(), userId, ContentType.ANSWER);

            // Prepare comment DTOs with votes
            List<GetDetailedCommentResponseDTO> commentResponseDTOs = answer.getComments().stream().map(comment -> {
                String commentUserVote = voteService.getUserVoteType(comment.getCommentId(), userId, ContentType.COMMENT);
                return DTOMapper.mapToDetailedCommentResponseDTO(comment, commentUserVote);
            }).collect(Collectors.toList());

            // Map answer, including votes and comments
            return DTOMapper.mapToDetailedAnswerResponseDTO(answer, answerUserVote, commentResponseDTOs);
        }).collect(Collectors.toList());

        // Map the question with the answers
        return DTOMapper.mapToDetailedQuestionResponseDTO(question, questionUserVote, mediaList, answerResponseDTOs);
    }

    public void updateVoteCount(Long questionId, VoteType voteType, int increment) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(GenericConstants.QUESTION_NOT_FOUND));

        if (voteType == VoteType.UPVOTE) {
            question.setUpvotes(question.getUpvotes() + increment);
        } else {
            question.setDownvotes(question.getDownvotes() + increment);
        }

        questionRepository.save(question);
    }

    @Override
    public String getQuestionContent(Long contentId) {
        Question question = questionRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.QUESTION_NOT_FOUND));

        return question.getBody();
    }

    @Override
    public void softDeleteQuestion(Long questionId, String deletedReason, Long moderatorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.QUESTION_NOT_FOUND));

        question.setDeleted(true);
        question.setDeletedAt(LocalDateTime.now());
        question.setDeletedBy(moderatorId);
        question.setContentStatus(ContentStatus.DELETED);

        questionRepository.save(question);
    }
}
