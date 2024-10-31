package com.forum.discussion_platform.service;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.ReportedContentRequestDTO;
import com.forum.discussion_platform.dto.response.ReportedContentResponseDTO;
import com.forum.discussion_platform.enums.ReportStatus;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.model.ReportedContent;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.ReportedContentRepository;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.util.DTOMapper;
import org.springframework.stereotype.Service;

@Service
public class ReportedContentService {
    private final ReportedContentRepository reportedContentRepository;
    private final UserRepository userRepository;

    public ReportedContentService(ReportedContentRepository reportedContentRepository, UserRepository userRepository) {
        this.reportedContentRepository = reportedContentRepository;
        this.userRepository = userRepository;
    }

    public ReportedContentResponseDTO createReport(ReportedContentRequestDTO requestDTO, Long userId) {
        User reportedBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

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
}
