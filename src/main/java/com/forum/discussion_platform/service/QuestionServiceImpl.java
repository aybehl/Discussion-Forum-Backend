package com.forum.discussion_platform.service;

import com.forum.discussion_platform.constants.MediaType;
import com.forum.discussion_platform.dto.QuestionRequestDTO;
import com.forum.discussion_platform.dto.QuestionResponseDTO;
import com.forum.discussion_platform.model.Media;
import com.forum.discussion_platform.model.Question;
import com.forum.discussion_platform.model.Tag;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.QuestionRepository;
import com.forum.discussion_platform.repository.TagRepository;
import com.forum.discussion_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final TagRepository tagRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               UserRepository userRepository,
                               TagRepository tagRepository){
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO) {
        User author = userRepository.findById(requestDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Tag> tags = tagRepository.findAllById(requestDTO.getTagIds());

        Question question = new Question();
        question.setTitle(requestDTO.getTitle());
        question.setBody(requestDTO.getBody());
        question.setAuthor(author);
        question.setTags(tags);

        Question savedQuestion = questionRepository.save(question);

        //TODO - Create entries in Media Table
//        List<Media> mediaList = requestDTO.getMediaUrls().stream()
//                .map(url -> new Media(MediaType.QUESTION, ))
//                .collect(Collectors.toList());
//
//        mediaRepository.saveAll(mediaList);

        QuestionResponseDTO responseDTO = new QuestionResponseDTO();
        responseDTO.setQuestionId(savedQuestion.getQuestionId());
        responseDTO.setTitle(savedQuestion.getTitle());
        responseDTO.setBody(savedQuestion.getBody());
        responseDTO.setTags(savedQuestion.getTags().stream().map(Tag::getName).collect(Collectors.toList()));  // Map tags to their names or IDs
        responseDTO.setAuthorId(author.getUserId());
        responseDTO.setCreatedAt(savedQuestion.getCreatedAt());
        responseDTO.setUpdatedAt(savedQuestion.getUpdatedAt());

        return responseDTO;
    }

    @Override
    public Question updateQuestion(Long id, Question updatedQuestion) {
        return null;
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
