package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
