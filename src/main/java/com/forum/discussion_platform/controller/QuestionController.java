package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.QuestionRequestDTO;
import com.forum.discussion_platform.dto.response.QuestionResponseDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.service.QuestionService;
import com.forum.discussion_platform.service.TokenService;
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
    public ResponseEntity<SuccessResponseDTO<QuestionResponseDTO>> createQuestion(@RequestPart("data") QuestionRequestDTO requestDTO,
                                                                                  @RequestPart("mediaFiles") List<MultipartFile> mediaFiles,
                                                                                  @RequestHeader("Authorization") String token){
        Long authorId = tokenService.getUserIdFromToken(token);

        QuestionResponseDTO questionResponseDTO = questionService.createQuestion(requestDTO, mediaFiles, authorId);

        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS, questionResponseDTO, HttpStatus.CREATED, GenericConstants.QUESTION_POSTED_SUCCESSFULLY), HttpStatus.CREATED);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<SuccessResponseDTO<QuestionResponseDTO>> updateQuestion(
            @PathVariable Long questionId,
            @RequestPart("data") EditQuestionRequestDTO requestDTO,
            @RequestPart(value = "newMediaFiles", required = false) List<MultipartFile> newMediaFiles,
            @RequestHeader("Authorization") String token) {

        Long authorId = tokenService.getUserIdFromToken(token);
        QuestionResponseDTO questionResponseDTO = questionService.updateQuestion(questionId, requestDTO, newMediaFiles, authorId);

        return ResponseEntity.ok(new SuccessResponseDTO<>(ApiStatus.SUCCESS, questionResponseDTO, HttpStatus.OK, GenericConstants.QUESTION_UPDATED_SUCCESSFULLY));
    }

}
