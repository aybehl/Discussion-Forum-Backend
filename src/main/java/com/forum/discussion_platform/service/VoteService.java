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
    public void createOrToggleVote(VoteRequestDTO request, Long authorId) {
        // Validate the author
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        // Validate the author in the request
        if (!request.getVotedById().equals(authorId)) {
            throw new IllegalArgumentException(GenericConstants.USER_DETAILS_MISMATCH);
        }

        // Check if a vote already exists for this content by this user
        Optional<Vote> existingVoteOpt = voteRepository.findByVotedBy_UserIdAndContentIdAndContentType(
                request.getVotedById(), request.getContentId(), request.getContentType());

        if (existingVoteOpt.isPresent()) {
            Vote existingVote = existingVoteOpt.get();

            if (existingVote.getVoteType().equals(request.getVoteType())) {
                // Case: The same vote type exists (e.g., upvote after an upvote) - Undo the vote
                undoVote(existingVote);
            } else {
                // Case: Opposite vote exists (e.g., upvote to downvote) - Switch vote type
                updateVoteCount(request, 1); // Increase count for new vote type
                VoteRequestDTO oppositeVoteRequest = VoteRequestDTO.builder()
                        .votedById(existingVote.getVotedBy().getUserId())
                        .contentId(existingVote.getContentId())
                        .contentType(existingVote.getContentType())
                        .voteType(existingVote.getVoteType())
                        .build();
                updateVoteCount(oppositeVoteRequest, -1); // Decrease count for old vote type
                existingVote.setVoteType(request.getVoteType()); // Update to new vote type
                voteRepository.save(existingVote);
            }
        } else {
            // Case: No existing vote - Create a new vote
            updateVoteCount(request, 1); // Increase the new vote count
            Vote newVote = Vote.builder()
                    .votedBy(user)
                    .voteType(request.getVoteType())
                    .contentId(request.getContentId())
                    .contentType(request.getContentType())
                    .build();
            voteRepository.save(newVote);
        }
    }

    @Transactional
    private void undoVote(Vote existingVote) {
        try {
            VoteRequestDTO request = VoteRequestDTO.builder()
                    .votedById(existingVote.getVotedBy().getUserId())
                    .contentId(existingVote.getContentId())
                    .contentType(existingVote.getContentType())
                    .voteType(existingVote.getVoteType())
                    .build();

            // Decrease the vote count in the related entity
            updateVoteCount(request, -1);

            // Delete the vote record
            voteRepository.delete(existingVote);
        } catch (Exception ex) {
            throw new RuntimeException(GenericConstants.UNDO_VOTE_FAILED, ex);
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
