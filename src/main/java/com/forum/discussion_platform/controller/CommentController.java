package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.CreateCommentRequestDTO;
import com.forum.discussion_platform.dto.request.EditCommentRequestDTO;
import com.forum.discussion_platform.dto.response.CommentResponseDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.service.CommentService;
import com.forum.discussion_platform.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final TokenService tokenService;

    public CommentController(CommentService commentService, TokenService tokenService) {
        this.commentService = commentService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<CommentResponseDTO>> createComment(@RequestBody CreateCommentRequestDTO requestDTO,
                                                                                @RequestHeader("Authorization") String token) {
        Long authorId = tokenService.getUserIdFromToken(token);

        CommentResponseDTO response = commentService.createComment(requestDTO, authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                response,
                HttpStatus.CREATED,
                GenericConstants.COMMENT_POSTED_SUCCESSFULLY),
                HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<SuccessResponseDTO<CommentResponseDTO>> editComment(@PathVariable Long commentId,
                                                          @RequestBody EditCommentRequestDTO requestDTO, @RequestHeader("Authorization") String token) {

        Long authorId = tokenService.getUserIdFromToken(token);

        CommentResponseDTO response = commentService.editComment(commentId, requestDTO.getBody(), authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                response,
                HttpStatus.OK,
                GenericConstants.COMMENT_UPDATED_SUCCESSFULLY),
                HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<SuccessResponseDTO> deleteComment(@PathVariable Long commentId,
                                                            @RequestHeader("Authorization") String token) {
        Long authorId = tokenService.getUserIdFromToken(token);

        commentService.deleteComment(commentId, authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                null,
                HttpStatus.OK,
                GenericConstants.COMMENT_DELETED_SUCCESSFULLY),
                HttpStatus.OK);
    }
}
