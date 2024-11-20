package com.forum.discussion_platform.service;

import com.forum.discussion_platform.dto.response.TagQuestionCountResponseDTO;
import com.forum.discussion_platform.dto.response.TagResponseDTO;
import com.forum.discussion_platform.model.Tag;
import com.forum.discussion_platform.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                .sorted(Comparator.comparing(TagResponseDTO::getTagName))
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
        if(tagIdsToDelete != null && !tagIdsToDelete.isEmpty()){
            List<Tag> tagsToRemove = currentTagList.stream()
                    .filter(tag -> tagIdsToDelete.contains(tag.getTagId()))
                    .toList();
            currentTagList.removeAll(tagsToRemove);
        }

        //Add new tags
        if(newTagIds != null && !newTagIds.isEmpty()){
            List<Tag> tagsToAdd = tagRepository.findAllById(newTagIds);
            currentTagList.addAll(tagsToAdd);
        }

        return currentTagList;
    }

    public boolean checkIfTagsUpdated(List<Tag> existingTags, List<Tag> updatedTags){
        Set<Long> setOfExistingTagIds = existingTags.stream().map(tag -> tag.getTagId()).collect(Collectors.toSet());

        for(Tag t: updatedTags){
            if(!setOfExistingTagIds.contains(t.getTagId())){
                return true;
            }
        }

        return false;
    }
}
