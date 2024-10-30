package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.AnswerRequestDTO;
import com.forum.discussion_platform.dto.response.AnswerResponseDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.service.AnswerService;
import com.forum.discussion_platform.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {
    private final AnswerService answerService;

    private final TokenService tokenService;

    public AnswerController(AnswerService answerService, TokenService tokenService) {
        this.answerService = answerService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<AnswerResponseDTO>> createAnswer(@RequestBody AnswerRequestDTO requestDTO,
                                                                              @RequestHeader("Authorization") String token){
        Long authorId = tokenService.getUserIdFromToken(token);

        AnswerResponseDTO responseDTO = answerService.createAnswer(requestDTO, authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                responseDTO,
                HttpStatus.CREATED,
                GenericConstants.ANSWER_POSTED_SUCCESSFULLY),
                HttpStatus.CREATED);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<SuccessResponseDTO<AnswerResponseDTO>> editAnswer(@PathVariable Long answerId,
                                                                            @RequestBody AnswerRequestDTO requestDTO,
                                                                            @RequestHeader("Authorization") String token){
        Long authorId = tokenService.getUserIdFromToken(token);

        AnswerResponseDTO responseDTO = answerService.editAnswer(answerId, requestDTO.getBody(), authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                responseDTO,
                HttpStatus.OK,
                GenericConstants.ANSWER_UPDATED_SUCCESSFULLY),
                HttpStatus.OK);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<SuccessResponseDTO> deleteAnswer(@PathVariable Long answerId, @RequestHeader("Authorization") String token){
        Long authorId = tokenService.getUserIdFromToken(token);

        answerService.deleteAnswer(answerId, authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                null,
                HttpStatus.OK,
                GenericConstants.ANSWER_DELETED_SUCCESSFULLY),
                HttpStatus.OK);
    }
}
