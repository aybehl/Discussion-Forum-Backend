package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
