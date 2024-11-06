package com.forum.discussion_platform.util;

import com.forum.discussion_platform.dto.response.*;
import com.forum.discussion_platform.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class DTOMapper {
    public static CreateOrEditQuestionResponseDTO mapToQuestionResponseDTOWithMedia(Question question, List<Media> mediaList){
        CreateOrEditQuestionResponseDTO responseDTO = new CreateOrEditQuestionResponseDTO();
        responseDTO.setQuestionId(question.getQuestionId());
        responseDTO.setTitle(question.getTitle());
        responseDTO.setBody(question.getBody());
        responseDTO.setTags(question.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        responseDTO.setAuthorId(question.getAuthor().getUserId());
        responseDTO.setCreatedAt(question.getCreatedAt());
        responseDTO.setUpdatedAt(question.getUpdatedAt());

        if(mediaList != null){
            responseDTO.setMediaUrls(mediaList.stream().map(Media::getMediaUrl).collect(Collectors.toList()));
        }

        return responseDTO;
    }

    public static AnswerResponseDTO mapToAnswerResponseDTO(Answer answer) {
        return AnswerResponseDTO.builder()
                .answerId(answer.getAnswerId())
                .body(answer.getBody())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .authorId(answer.getAnsweredBy().getUserId())
                .questionId(answer.getRelatedQuestion().getQuestionId())
                .build();
    }

    public static CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getBody())
                .authorId(comment.getCommentedBy().getUserId())
                .answerId(comment.getRelatedAnswer().getAnswerId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static GetDetailedQuestionResponseDTO mapToDetailedQuestionResponseDTO(Question question, String userVoteType, List<GetDetailedAnswerResponseDTO> answers){
        return GetDetailedQuestionResponseDTO.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .body(question.getBody())
                .tags(question.getTags().stream()
                        .map(tag -> new TagResponseDTO(tag.getTagId(), tag.getName(), tag.getDescription()))
                        .collect(Collectors.toList()))
                .votes(mapToVoteResponse(question.getUpvotes(), question.getDownvotes(), userVoteType))
                .answers(answers)
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .author(UserResponseDTO.builder()
                        .userId(question.getAuthor().getUserId())
                        .username(question.getAuthor().getUserName())
                        .build())
                .build();
    }

    public static GetDetailedAnswerResponseDTO mapToDetailedAnswerResponseDTO(Answer answer, String userVoteType, List<GetDetailedCommentResponseDTO> comments){
        return GetDetailedAnswerResponseDTO.builder()
                .answerId(answer.getAnswerId())
                .body(answer.getBody())
                .createdAt(answer.getCreatedAt())
                .author(UserResponseDTO.builder()
                        .userId(answer.getAnsweredBy().getUserId())
                        .username(answer.getAnsweredBy().getUserName())
                        .build())
                .votes(mapToVoteResponse(answer.getUpvotes(), answer.getDownvotes(), userVoteType))
                .comments(comments)
                .isDeleted(answer.isDeleted())
                .deletedReason(answer.getDeletedReason())
                .build();
    }

    public static GetDetailedCommentResponseDTO mapToDetailedCommentResponseDTO(Comment comment, String commentUserVote){
        return GetDetailedCommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .body(comment.getBody())
                .createdAt(comment.getCreatedAt())
                .author(UserResponseDTO.builder()
                        .userId(comment.getCommentedBy().getUserId())
                        .username(comment.getCommentedBy().getUserName())
                        .build())
                .votes(mapToVoteResponse(comment.getUpvotes(), comment.getDownvotes(), commentUserVote))
                .isDeleted(comment.isDeleted())
                .deletedReason(comment.getDeletedReason())
                .build();
    }

    private static VoteResponseDTO mapToVoteResponse(int upvotes, int downvotes, String userVote) {
        return VoteResponseDTO.builder()
                .upvotes(upvotes)
                .downvotes(downvotes)
                .userVote(userVote)
                .build();
    }

    public static ReportedContentResponseDTO mapToReportedContentResponseDTO(ReportedContent reportedContent) {
        return ReportedContentResponseDTO.builder()
                .reportId(reportedContent.getReportId())
                .contentType(reportedContent.getContentType())
                .contentId(reportedContent.getContentId())
                .reportedById(reportedContent.getReportedBy().getUserId())
                .reason(reportedContent.getReason())
                .additionalComment(reportedContent.getAdditionalComment())
                .status(reportedContent.getStatus())
                .createdAt(reportedContent.getCreatedAt())
                .build();
    }

    public static UserProfileResponseDTO mapToUserProfileDTO(User user){
        return UserProfileResponseDTO.builder()
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .joinedAt(user.getCreatedAt())
                .build();
    }
}
