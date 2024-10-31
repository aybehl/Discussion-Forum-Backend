package com.forum.discussion_platform.repository;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v.voteType FROM Vote v WHERE v.contentId = :contentId AND v.votedBy.userId = :userId AND v.contentType = :contentType")
    Optional<String> findUserVoteType(@Param("contentId") Long contentId, @Param("userId") Long userId, @Param("contentType") ContentType contentType);
}
