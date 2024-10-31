package com.forum.discussion_platform.service;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.VoteType;
import com.forum.discussion_platform.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public String getUserVoteType(Long contentId, Long userId, ContentType contentType) {
        if (userId == null) {
            return VoteType.NONE.toString();
        }
        return voteRepository.findUserVoteType(contentId, userId, contentType).orElse(VoteType.NONE.toString());
    }
}
