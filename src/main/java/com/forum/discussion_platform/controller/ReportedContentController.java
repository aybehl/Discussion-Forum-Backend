package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.ModerationRequestDTO;
import com.forum.discussion_platform.dto.request.ReportedContentRequestDTO;
import com.forum.discussion_platform.dto.response.GetReportedContentResponseDTO;
import com.forum.discussion_platform.dto.response.ReportedContentResponseDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.enums.ReportReason;
import com.forum.discussion_platform.enums.ReportStatus;
import com.forum.discussion_platform.enums.UserRole;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import com.forum.discussion_platform.service.ReportedContentService;
import com.forum.discussion_platform.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/report-content")
public class ReportedContentController {
    private final ReportedContentService reportedContentService;

    private final TokenService tokenService;

    public ReportedContentController(ReportedContentService reportedContentService, TokenService tokenService) {
        this.reportedContentService = reportedContentService;
        this.tokenService = tokenService;
    }

    @GetMapping("/reasons")
    public ResponseEntity<SuccessResponseDTO<List<ReportReason>>> getReportReasons() {
        return ResponseEntity.ok(new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                Arrays.asList(ReportReason.values()),
                HttpStatus.OK,
                GenericConstants.REPORT_REASONS_RETRIEVED_SUCCESSFULLY));
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<ReportedContentResponseDTO>> createReport(@RequestBody ReportedContentRequestDTO requestDTO,
                                                                                       @RequestHeader("Authorization") String token) {

        Long authorId = tokenService.getUserIdFromToken(token);

        ReportedContentResponseDTO responseDTO = reportedContentService.createReport(requestDTO, authorId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS, responseDTO, HttpStatus.CREATED, GenericConstants.CONTENT_REPORTED_SUCCESSFULLY), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<Page<GetReportedContentResponseDTO>>> getAllReportedContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {

        Long userId = tokenService.getUserIdFromToken(token);
        UserRole userRole = tokenService.getUserRoleFromToken(token);

        if(!UserRole.MODERATOR.equals(userRole)){
            throw new UnauthorizedAccessException(GenericConstants.RESOURCE_ACCESS_ONLY_FOR_MODERATOR);
        }

        Page<GetReportedContentResponseDTO> paginatedReports = reportedContentService.getAllReportedContent(userId, page, size);

        return new ResponseEntity<>(
                new SuccessResponseDTO<>(ApiStatus.SUCCESS, paginatedReports, HttpStatus.OK, GenericConstants.REPORTED_CONTENT_RETRIEVED_SUCCESSFULLY),
                HttpStatus.OK
        );
    }

    @PutMapping("/{reportId}/moderate")
    public ResponseEntity<SuccessResponseDTO<String>> moderateReportedContent(
            @PathVariable Long reportId,
            @RequestBody ModerationRequestDTO requestDTO,
            @RequestHeader("Authorization") String token) {

        Long moderatorId = tokenService.getUserIdFromToken(token);
        UserRole userRole = tokenService.getUserRoleFromToken(token);

        if (!UserRole.MODERATOR.equals(userRole)) {
            throw new UnauthorizedAccessException(GenericConstants.RESOURCE_ACCESS_ONLY_FOR_MODERATOR);
        }

        reportedContentService.moderateReportedContent(reportId, requestDTO, moderatorId);

        String message = requestDTO.getAction() == ReportStatus.VERIFIED
                ? GenericConstants.REPORT_VERIFIED_SUCCESSFULLY
                : GenericConstants.REPORT_DISMISSED_SUCCESSFULLY;

        return new ResponseEntity<>(
                new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                        null,
                        HttpStatus.OK,
                        message),
                HttpStatus.OK
        );
    }

}
