package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.ReportedContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedContentRepository extends JpaRepository<ReportedContent, Long> {

}
