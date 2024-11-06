package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.response.TagQuestionCountResponseDTO;
import com.forum.discussion_platform.dto.response.TagResponseDTO;
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

    public List<TagResponseDTO> getAllTags(){
        List<Tag> tagList = tagRepository.findAll();

        return tagList.stream()
                .map(tag -> new TagResponseDTO(tag.getTagId(), tag.getName(), tag.getDescription()))
                .collect(Collectors.toList());
    }

    public List<TagQuestionCountResponseDTO> getAllTagsWithQuestionCount(){
        List<Object[]> result = tagRepository.findTagsWithQuestionCount();

        return result.stream()
                .map(row -> new TagQuestionCountResponseDTO((Long) row[0], (String) row[1], (Long) row[2]))
                .collect(Collectors.toList());
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
