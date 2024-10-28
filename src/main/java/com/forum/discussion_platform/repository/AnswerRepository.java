package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
