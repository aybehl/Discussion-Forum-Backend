package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.VoteRequestDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.service.TokenService;
import com.forum.discussion_platform.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    private final TokenService tokenService;

    @Autowired
    public VoteController(VoteService voteService, TokenService tokenService) {
        this.voteService = voteService;
        this.tokenService = tokenService;
    }

    @PostMapping("/create")
    public ResponseEntity<SuccessResponseDTO<String>> createVote(
            @RequestBody VoteRequestDTO voteRequest, @RequestHeader("Authorization") String token) {
        Long authorId = tokenService.getUserIdFromToken(token);

        voteService.createVote(voteRequest, authorId);
        return ResponseEntity.ok(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                null,
                HttpStatus.OK,
                GenericConstants.VOTE_REGISTERED_SUCCESSFULLY));
    }

    @DeleteMapping("/undo")
    public ResponseEntity<SuccessResponseDTO<String>> undoVote(
            @RequestBody VoteRequestDTO voteRequest, @RequestHeader("Authorization") String token) {
        Long authorId = tokenService.getUserIdFromToken(token);

        voteService.undoVote(voteRequest, authorId);
        return ResponseEntity.ok(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                null,
                HttpStatus.OK,
                GenericConstants.VOTE_UNREGISTERED_SUCCESSFULLY));
    }
}