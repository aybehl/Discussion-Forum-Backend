package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.dto.response.GetQuestionResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.QuestionRequestDTO;
import com.forum.discussion_platform.dto.response.CreateOrEditQuestionResponseDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.service.QuestionService;
import com.forum.discussion_platform.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final TokenService tokenService;

    @Autowired
    public QuestionController(QuestionService questionService,
                              TokenService tokenService){
        this.questionService = questionService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<CreateOrEditQuestionResponseDTO>> createQuestion(@RequestPart("data") QuestionRequestDTO requestDTO,
                                                                                              @RequestPart("mediaFiles") List<MultipartFile> mediaFiles,
                                                                                              @RequestHeader("Authorization") String token){
        Long authorId = tokenService.getUserIdFromToken(token);

        CreateOrEditQuestionResponseDTO createOrEditQuestionResponseDTO = questionService.createQuestion(requestDTO, mediaFiles, authorId);

        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS, createOrEditQuestionResponseDTO, HttpStatus.CREATED, GenericConstants.QUESTION_POSTED_SUCCESSFULLY), HttpStatus.CREATED);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<SuccessResponseDTO<CreateOrEditQuestionResponseDTO>> updateQuestion(
            @PathVariable Long questionId,
            @RequestPart("data") EditQuestionRequestDTO requestDTO,
            @RequestPart(value = "newMediaFiles", required = false) List<MultipartFile> newMediaFiles,
            @RequestHeader("Authorization") String token) {

        Long authorId = tokenService.getUserIdFromToken(token);
        CreateOrEditQuestionResponseDTO createOrEditQuestionResponseDTO = questionService.updateQuestion(questionId, requestDTO, newMediaFiles, authorId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(ApiStatus.SUCCESS, createOrEditQuestionResponseDTO, HttpStatus.OK, GenericConstants.QUESTION_UPDATED_SUCCESSFULLY));
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<Page<GetQuestionResponseDTO>>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<GetQuestionResponseDTO> questions = questionService.getAllQuestionsWithPagination(page, size);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS, questions, HttpStatus.OK, GenericConstants.QUESTION_RETRIEVED_SUCCESSFULLY), HttpStatus.OK);
    }

    @GetMapping("/tags")
    public ResponseEntity<SuccessResponseDTO<List<CreateOrEditQuestionResponseDTO>>> getQuestionsByTags(@RequestParam List<Long> tagsIds){
        List<CreateOrEditQuestionResponseDTO> questions = questionService.getQuestionsByTags(tagsIds);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS, questions, HttpStatus.OK, GenericConstants.QUESTION_RETRIEVED_SUCCESSFULLY), HttpStatus.OK);
    }

}
