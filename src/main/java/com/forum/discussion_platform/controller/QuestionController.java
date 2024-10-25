package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.ApiStatus;
import com.forum.discussion_platform.dto.QuestionRequestDTO;
import com.forum.discussion_platform.dto.QuestionResponseDTO;
import com.forum.discussion_platform.dto.SuccessResponseDTO;
import com.forum.discussion_platform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService){
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<QuestionResponseDTO>> createQuestion(@RequestBody QuestionRequestDTO requestDTO,
                                                                                  @RequestPart("mediaFiles") List<MultipartFile> mediaFiles){
        //TODO - Add Authorization Logic, set author Id here in the request DTO

        //TODO - Upload media files to Cloudinary and get the URLs
        //List<String> mediaUrls = cloudinaryService.uploadFiles(mediaFiles);
        //requestDTO.setMediaUrls(mediaUrls);

        QuestionResponseDTO questionResponseDTO = questionService.createQuestion(requestDTO);
        return new ResponseEntity<>(new SuccessResponseDTO<>(ApiStatus.SUCCESS, questionResponseDTO, HttpStatus.CREATED, "Question posted successfully"), HttpStatus.CREATED);
    }

}
