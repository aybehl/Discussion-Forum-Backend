package com.forum.discussion_platform.controller;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.EditQuestionRequestDTO;
import com.forum.discussion_platform.dto.request.EditUserProfileRequestDTO;
import com.forum.discussion_platform.dto.response.SuccessResponseDTO;
import com.forum.discussion_platform.dto.response.UserProfileResponseDTO;
import com.forum.discussion_platform.enums.ApiStatus;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import com.forum.discussion_platform.service.TokenService;
import com.forum.discussion_platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<SuccessResponseDTO<UserProfileResponseDTO>> getUserProfile(@RequestHeader("Authorization") String token) {
        Long userId = tokenService.getUserIdFromToken(token);
        UserProfileResponseDTO userProfile = userService.getUserProfile(userId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                userProfile,
                HttpStatus.OK,
                GenericConstants.USER_PROFILE_FETCHED_SUCCESSFULLY),
                HttpStatus.OK);
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<SuccessResponseDTO<UserProfileResponseDTO>> editUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestPart("data") EditUserProfileRequestDTO request,
            @RequestPart(value = "profilePicFile", required = false) MultipartFile profilePic) {
        Long userId = tokenService.getUserIdFromToken(token);

        UserProfileResponseDTO updatedProfile = userService.editUserProfile(userId, request, profilePic);
        return new ResponseEntity<>(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                updatedProfile,
                HttpStatus.OK,
                GenericConstants.USER_PROFILE_UPDATED_SUCCESSFULLY),
                HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<SuccessResponseDTO<String>> deleteUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {

        Long authenticatedUserId = tokenService.getUserIdFromToken(token);

        // Ensures the authenticated user is deleting their own account
        if (!authenticatedUserId.equals(userId)) {
            throw new UnauthorizedAccessException(GenericConstants.UNAUTHORISED_DELETE_USER_ACTION);
        }

        userService.deleteUser(userId);
        return new ResponseEntity<>(new SuccessResponseDTO<>(
                ApiStatus.SUCCESS,
                null,
                HttpStatus.OK,
                GenericConstants.USER_DELETED_SUCCESSFULLY),
                HttpStatus.OK);
    }


}
