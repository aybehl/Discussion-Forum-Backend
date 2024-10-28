package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.model.Question;
import com.forum.discussion_platform.model.Tag;
import com.forum.discussion_platform.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> manageTagsForQuestion(List<Long> newTagIds, List<Long> tagIdsToDelete, List<Tag> currentTagList){
        //Remove tags to delete
        List<Tag> tagsToRemove = currentTagList.stream()
                .filter(tag -> tagIdsToDelete.contains(tag.getTagId()))
                .collect(Collectors.toList());
        currentTagList.removeAll(tagsToRemove);

        //Add new tags
        List<Tag> tagsToAdd = tagRepository.findAllById(newTagIds);
        currentTagList.addAll(tagsToAdd);

        return currentTagList;
    }
}
