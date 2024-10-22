package com.forum.discussion_platform.model;

import com.forum.discussion_platform.constants.ContentType;
import com.forum.discussion_platform.constants.ReportReason;
import com.forum.discussion_platform.constants.ReportStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reported_Contents")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ReportedContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_id", nullable = false)
    private User reportedBy;

    @Column(name = "reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(name="additional_comment", columnDefinition = "TEXT")
    private String additionalComment;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}