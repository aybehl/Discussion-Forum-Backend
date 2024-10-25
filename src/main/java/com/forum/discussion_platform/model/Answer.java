package com.forum.discussion_platform.model;

import com.forum.discussion_platform.constants.ContentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Answers")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answered_by_id", nullable = false)
    private User answeredBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_question_id", nullable = false)
    private Question relatedQuestion;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @OneToMany(mappedBy = "relatedAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Column(name = "upvotes", nullable = false)
    private int upvotes = 0;

    @Column(name = "downvotes", nullable = false)
    private int downvotes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContentStatus contentStatus = ContentStatus.ACTIVE;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_reason", columnDefinition = "TEXT")
    private String deletedReason;

    @Column(name = "deleted_by_moderator_id")
    private Long deletedByModeratorId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
