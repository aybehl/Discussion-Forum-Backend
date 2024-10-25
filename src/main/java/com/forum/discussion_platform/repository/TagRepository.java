package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
