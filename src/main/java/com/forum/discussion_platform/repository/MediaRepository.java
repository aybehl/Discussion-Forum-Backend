package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {

    List<Media> findByContentIdAndContentType(Long contentId, ContentType contentType);
}
