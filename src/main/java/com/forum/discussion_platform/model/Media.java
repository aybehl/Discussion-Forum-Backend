package com.forum.discussion_platform.model;

import com.forum.discussion_platform.constants.ContentType;
import com.forum.discussion_platform.constants.MediaType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "Media")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long media_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Media(ContentType contentType, Long contentId, MediaType mediaType, String mediaUrl) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
    }
}
