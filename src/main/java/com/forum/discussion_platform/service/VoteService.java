package com.forum.discussion_platform.service;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.VoteRequestDTO;
import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.VoteType;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.model.Vote;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @Autowired
    public VoteService(VoteRepository voteRepository, UserRepository userRepository, @Lazy QuestionService questionService,
                       AnswerService answerService, CommentService commentService) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.questionService = questionService;
        this.answerService = answerService;
        this.commentService = commentService;
    }

    public String getUserVoteType(Long contentId, Long userId, ContentType contentType) {
        if (userId == null) {
            return VoteType.NONE.toString();
        }
        return voteRepository.findUserVoteType(contentId, userId, contentType).orElse(VoteType.NONE.toString());
    }

    @Transactional
    public void createVote(VoteRequestDTO request, Long authorId) {
        try {
            // Validate the author
            User user = userRepository.findById(authorId)
                    .orElseThrow(() ->  new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

            // Validate the author
            if (!request.getVotedById().equals(authorId)) {
                throw new IllegalArgumentException(GenericConstants.USER_DETAILS_MISMATCH);
            }

            // Check if vote already exists
            Optional<Vote> existingVote = voteRepository.findByVotedBy_UserIdAndContentIdAndContentType(
                    request.getVotedById(), request.getContentId(), request.getContentType()
            );

            if (existingVote.isPresent()) {
                throw new IllegalStateException(GenericConstants.VOTE_ALREADY_EXISTS);
            }

            // Update the vote count in the related entity
            updateVoteCount(request, 1);

            // Create and save the new vote
            Vote vote = Vote.builder()
                    .votedBy(user)
                    .voteType(request.getVoteType())
                    .contentId(request.getContentId())
                    .contentType(request.getContentType())
                    .build();

            voteRepository.save(vote);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public void undoVote(VoteRequestDTO request, Long authorId) {
        try {
            // Validate the author
            User user = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

            // Validate the author in request
            if (!request.getVotedById().equals(authorId)) {
                throw new IllegalArgumentException(GenericConstants.USER_DETAILS_MISMATCH);
            }

            // Check if vote exists
            Vote existingVote = voteRepository.findByVotedBy_UserIdAndContentIdAndContentType(
                    request.getVotedById(), request.getContentId(), request.getContentType()
            ).orElseThrow(() -> new ResourceNotFoundException(GenericConstants.VOTE_NOT_FOUND));

            // Update the vote count in the related entity
            updateVoteCount(request, -1);

            // Delete the vote
            voteRepository.delete(existingVote);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private void updateVoteCount(VoteRequestDTO request, int increment) {
        switch (request.getContentType()) {
            case QUESTION:
                questionService.updateVoteCount(request.getContentId(), request.getVoteType(), increment);
                break;
            case ANSWER:
                answerService.updateVoteCount(request.getContentId(), request.getVoteType(), increment);
                break;
            case COMMENT:
                commentService.updateVoteCount(request.getContentId(), request.getVoteType(), increment);
                break;
        }
    }
}
