package com.forum.discussion_platform.service;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.ModerationRequestDTO;
import com.forum.discussion_platform.dto.request.ReportedContentRequestDTO;
import com.forum.discussion_platform.dto.response.GetReportedContentResponseDTO;
import com.forum.discussion_platform.dto.response.ReportedContentResponseDTO;
import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.ReportStatus;
import com.forum.discussion_platform.exception.ContentAlreadyModeratedException;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.model.ReportedContent;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.ReportedContentRepository;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.util.DTOMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReportedContentService {
    private final ReportedContentRepository reportedContentRepository;
    private final UserRepository userRepository;

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    public ReportedContentService(ReportedContentRepository reportedContentRepository, UserRepository userRepository, QuestionService questionService, AnswerService answerService, CommentService commentService) {
        this.reportedContentRepository = reportedContentRepository;
        this.userRepository = userRepository;
        this.questionService = questionService;
        this.answerService = answerService;
        this.commentService = commentService;
    }

    public ReportedContentResponseDTO createReport(ReportedContentRequestDTO requestDTO, Long userId) {
        User reportedBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        if(requestDTO.getContentType() == null || requestDTO.getContentId() == null
            || requestDTO.getReason() == null){
            throw new IllegalArgumentException(GenericConstants.INVALID_CONTENT_FOR_REPORTED_CONTENT);
        }

        ReportedContent reportedContent = new ReportedContent();
        reportedContent.setContentType(requestDTO.getContentType());
        reportedContent.setContentId(requestDTO.getContentId());
        reportedContent.setReportedBy(reportedBy);
        reportedContent.setReason(requestDTO.getReason());
        reportedContent.setAdditionalComment(requestDTO.getAdditionalComment());
        reportedContent.setStatus(ReportStatus.PENDING);

        ReportedContent savedReport = reportedContentRepository.save(reportedContent);

        return DTOMapper.mapToReportedContentResponseDTO(savedReport);
    }

    public Page<GetReportedContentResponseDTO> getAllReportedContent(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        return reportedContentRepository.findAll(pageable)
                .map(report -> GetReportedContentResponseDTO.builder()
                        .reportId(report.getReportId())
                        .contentType(report.getContentType())
                        .contentId(report.getContentId())
                        .reportedById(report.getReportedBy().getUserId())
                        .contentSnippet(getContentSnippet(report.getContentId(), report.getContentType()))
                        .reason(report.getReason())
                        .additionalComment(report.getAdditionalComment())
                        .status(report.getStatus())
                        .reportedOn(report.getCreatedAt())
                        .build()
                );
    }

    private String getContentSnippet(Long contentId, ContentType contentType) {
        String content;
        switch (contentType) {
            case QUESTION:
                content = questionService.getQuestionContent(contentId);
                break;
            case ANSWER:
                content = answerService.getAnswerContent(contentId);
                break;
            case COMMENT:
                content = commentService.getCommentContent(contentId);
                break;
            default:
                throw new IllegalArgumentException("Unknown content type: " + contentType);
        }
        return snippetText(content);
    }

    private String snippetText(String content) {
        int snippetLength = 50;
        return content != null && content.length() > snippetLength
                ? content.substring(0, snippetLength) + "..."
                : content;
    }

    @Transactional
    public void moderateReportedContent(Long reportId, ModerationRequestDTO requestDTO, Long moderatorId) {
        ReportedContent reportedContent = reportedContentRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.REPORTED_CONTENT_NOT_FOUND));

        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        if(reportedContent.getStatus() != null && reportedContent.getStatus() != ReportStatus.PENDING){
            throw new ContentAlreadyModeratedException(GenericConstants.REPORT_ALREADY_MODERATED);
        }

        reportedContent.setModerator(moderator);
        reportedContent.setReviewedAt(LocalDateTime.now());


        if (requestDTO.getAction() == ReportStatus.VERIFIED) {
            if (requestDTO.getDeletedReason() == null || requestDTO.getDeletedReason().isEmpty()) {
                throw new IllegalArgumentException(GenericConstants.REASON_REQUIRED_FOR_VERIFICATION);
            }

            reportedContent.setStatus(ReportStatus.VERIFIED);
            handleModeratedContent(reportedContent, requestDTO.getDeletedReason());
        } else if (requestDTO.getAction() == ReportStatus.DISMISSED) {
            reportedContent.setStatus(ReportStatus.DISMISSED);
        } else {
            throw new IllegalArgumentException(GenericConstants.INCORRECT_REPORT_STATUS);
        }

        reportedContentRepository.save(reportedContent);
    }

    private void handleModeratedContent(ReportedContent reportedContent, String deletedReason) {
        ContentType contentType = reportedContent.getContentType();
        Long contentId = reportedContent.getContentId();
        Long moderatorId = reportedContent.getModerator().getUserId();

        switch (contentType) {
            case QUESTION:
                questionService.softDeleteQuestion(contentId, deletedReason, moderatorId);
                break;
            case ANSWER:
                answerService.softDeleteAnswer(contentId, deletedReason, moderatorId);
                break;
            case COMMENT:
                commentService.softDeleteComment(contentId, deletedReason, moderatorId);
                break;
            default:
                throw new IllegalArgumentException("Unsupported content type for deletion: " + contentType);
        }
    }

}
