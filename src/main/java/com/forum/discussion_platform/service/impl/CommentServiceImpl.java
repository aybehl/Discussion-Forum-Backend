package com.forum.discussion_platform.service.impl;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.CreateCommentRequestDTO;
import com.forum.discussion_platform.dto.response.CommentResponseDTO;
import com.forum.discussion_platform.enums.ContentStatus;
import com.forum.discussion_platform.enums.VoteType;
import com.forum.discussion_platform.exception.ContentAlreadyDeleted;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import com.forum.discussion_platform.model.Answer;
import com.forum.discussion_platform.model.Comment;
import com.forum.discussion_platform.model.Question;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.AnswerRepository;
import com.forum.discussion_platform.repository.CommentRepository;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.service.CommentService;
import com.forum.discussion_platform.util.DTOMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }
    @Override
    public CommentResponseDTO createComment(CreateCommentRequestDTO requestDTO, Long authorId) {
        User author = userRepository.findById(requestDTO.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        Answer answer = answerRepository.findById(requestDTO.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.ANSWER_NOT_FOUND));

        if (requestDTO.getBody() == null || requestDTO.getBody().isEmpty()) {
            throw new IllegalArgumentException(GenericConstants.INVALID_CONTENT_FOR_COMMENT);
        }

        Comment comment = new Comment();
        comment.setBody(requestDTO.getBody());
        comment.setCommentedBy(author);
        comment.setRelatedAnswer(answer);

        Comment savedComment = commentRepository.save(comment);
        return DTOMapper.mapToCommentResponseDTO(savedComment);
    }

    @Override
    public CommentResponseDTO editComment(Long commentId, String newBody, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.COMMENT_NOT_FOUND));

        if (!comment.getCommentedBy().getUserId().equals(authorId)) {
            throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_COMMENT_UPDATE);
        }

        if (comment.isDeleted()) {
            throw new ContentAlreadyDeleted(GenericConstants.COMMENT_ALREADY_DELETED);
        }

        if (newBody != null && !newBody.isEmpty()) {
            comment.setBody(newBody);
        } else {
            throw new IllegalArgumentException(GenericConstants.INVALID_CONTENT_FOR_COMMENT);
        }

        Comment updatedComment = commentRepository.save(comment);
        return DTOMapper.mapToCommentResponseDTO(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.COMMENT_NOT_FOUND));

        if (!comment.getCommentedBy().getUserId().equals(authorId)) {
            throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_COMMENT_DELETE);
        }

        if (comment.isDeleted()) {
            throw new ContentAlreadyDeleted(GenericConstants.COMMENT_ALREADY_DELETED);
        }

        comment.setDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        comment.setContentStatus(ContentStatus.DELETED);
        comment.setDeletedBy(authorId);
        comment.setDeletedReason(GenericConstants.DELETED_BY_AUTHOR);

        commentRepository.save(comment);
    }

    public void updateVoteCount(Long commentId, VoteType voteType, int increment) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(GenericConstants.COMMENT_NOT_FOUND));

        if (voteType == VoteType.UPVOTE) {
            comment.setUpvotes(comment.getUpvotes() + increment);
        } else {
            comment.setDownvotes(comment.getDownvotes() + increment);
        }

        commentRepository.save(comment);
    }

    @Override
    public String getCommentContent(Long contentId) {
        Comment comment = commentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.COMMENT_NOT_FOUND));

        return comment.getBody();
    }

    @Override
    public void softDeleteComment(Long contentId, String deletedReason, Long moderatorId) {
        Comment comment = commentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException(GenericConstants.COMMENT_NOT_FOUND));

        comment.setDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        comment.setContentStatus(ContentStatus.DELETED);
        comment.setDeletedBy(moderatorId);
        comment.setDeletedReason(deletedReason);

        commentRepository.save(comment);
    }
}


