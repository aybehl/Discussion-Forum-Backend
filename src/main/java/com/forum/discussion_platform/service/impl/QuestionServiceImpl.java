package com.forum.discussion_platform.service.impl;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.dto.request.QuestionRequestDTO;
import com.forum.discussion_platform.dto.response.QuestionResponseDTO;
import com.forum.discussion_platform.enums.ContentType;
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
import com.forum.discussion_platform.util.DTOMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    private final MediaService mediaService;
    private final TagService tagService;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               UserRepository userRepository,
                               TagRepository tagRepository,
                               MediaService mediaService,
                               TagService tagService){
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.mediaService = mediaService;
        this.tagService = tagService;
    }

    @Override
    @Transactional
    public QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO, List<MultipartFile> mediaFiles, Long authorId) {
        try {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException(GenericConstants.USER_NOT_FOUND));

            List<Tag> tags = tagRepository.findAllById(requestDTO.getTagIds());

            Question question = new Question();
            question.setTitle(requestDTO.getTitle());
            question.setBody(requestDTO.getBody());
            question.setAuthor(author);
            question.setTags(tags);

            Question savedQuestion = questionRepository.save(question);

            List<Media> mediaList = mediaService.processAndSaveMediaFiles(mediaFiles, savedQuestion.getQuestionId(), ContentType.QUESTION);
            return DTOMapper.mapToQuestionResponseDTO(savedQuestion, mediaList);
        } catch(Exception ex){
            // Rollback transaction if media upload fails
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Transactional
    public QuestionResponseDTO updateQuestion(Long questionId, EditQuestionRequestDTO requestDTO, List<MultipartFile> newMediaFiles, Long authorId) {
        try {
            //Validate the question
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException(GenericConstants.QUESTION_NOT_FOUND));

            // Validate the author
            if (!question.getAuthor().getUserId().equals(authorId)) {
                throw new UnauthorizedAccessException("You can only edit your own questions.");
            }

            if(requestDTO.getTitle() != null){
                question.setTitle(requestDTO.getTitle());
            }
            if(requestDTO.getBody() != null){
                question.setBody(requestDTO.getBody());
            }

            List<Tag> updatedTags = tagService.manageTagsForQuestion(requestDTO.getTagsToDelete(),
                    requestDTO.getNewTagIds(), question.getTags());

            question.setTags(updatedTags);

            Question savedQuestion = questionRepository.save(question);

            List<Media> updatedMedia = mediaService.manageMedia(requestDTO.getMediaToDelete(), newMediaFiles, question.getQuestionId(), ContentType.QUESTION);
            return DTOMapper.mapToQuestionResponseDTO(savedQuestion, updatedMedia);
        } catch(Exception ex){
            // Rollback transaction if media upload fails
            throw new ResourceUpdateException(ContentType.QUESTION, questionId, "Failed to update due to an internal error.");
        }
    }


    @Override
    public Question getQuestion(Long id) {
        return null;
    }

    @Override
    public List<Question> getAllQuestions(int page, int size) {
        return null;
    }

    @Override
    public void deleteQuestion(Long id) {

    }
}
