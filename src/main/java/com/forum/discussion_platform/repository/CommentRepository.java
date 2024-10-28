package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
