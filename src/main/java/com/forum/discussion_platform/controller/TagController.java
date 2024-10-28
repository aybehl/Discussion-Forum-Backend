package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.dto.response.TagQuestionCountResponseDTO;
import com.forum.discussion_platform.dto.response.TagResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.service.TagService;
import com.sun.net.httpserver.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<TagResponseDTO>>> getAllTags(){
        List<TagResponseDTO> tags = tagService.getAllTags();
        return new ResponseEntity<>(
                new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                        tags,
                        HttpStatus.OK,
                        GenericConstants.TAGS_FETCHED_SUCCESSFULLY),
                HttpStatus.OK);
    }

    @GetMapping("/with-count")
    public ResponseEntity<SuccessResponseDTO<List<TagQuestionCountResponseDTO>>> getAllTagsWithQuestionCount(){
        List<TagQuestionCountResponseDTO> tags = tagService.getAllTagsWithQuestionCount();
        return new ResponseEntity<>(
                new SuccessResponseDTO<>(ApiStatus.SUCCESS,
                        tags,
                        HttpStatus.OK,
                        GenericConstants.TAGS_FETCHED_SUCCESSFULLY),
                HttpStatus.OK);
    }


}
