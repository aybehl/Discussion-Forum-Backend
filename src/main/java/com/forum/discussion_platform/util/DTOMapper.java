package com.forum.discussion_platform.util;

import com.forum.discussion_platform.dto.response.AnswerResponseDTO;
import com.forum.discussion_platform.dto.response.CommentResponseDTO;
import com.forum.discussion_platform.dto.response.CreateOrEditQuestionResponseDTO;
import com.forum.discussion_platform.dto.response.GetQuestionResponseDTO;
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
        responseDTO.setMediaUrls(mediaList.stream().map(Media::getMediaUrl).collect(Collectors.toList()));

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
}
