package com.forum.discussion_platform.service;

import com.forum.discussion_platform.constants.GenericConstants;
import com.forum.discussion_platform.dto.request.EditUserProfileRequestDTO;
import com.forum.discussion_platform.dto.response.UserProfileResponseDTO;
import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.exception.ResourceNotFoundException;
import com.forum.discussion_platform.model.Media;
import com.forum.discussion_platform.model.User;
import com.forum.discussion_platform.repository.UserRepository;
import com.forum.discussion_platform.util.DTOMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MediaService mediaService;

    public UserService(UserRepository userRepository, MediaService mediaService) {
        this.userRepository = userRepository;
        this.mediaService = mediaService;
    }

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        return DTOMapper.mapToUserProfileDTO(user);
    }

    @Transactional
    public UserProfileResponseDTO editUserProfile(Long userId, EditUserProfileRequestDTO request, MultipartFile profilePic){
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

            if (request.getUserName() != null && !request.getUserName().trim().isEmpty()) {
                user.setUserName(request.getUserName());
            }
            if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
                user.setLastName(request.getLastName());
            }
            if (request.getBio() != null && !request.getBio().trim().isEmpty()) {
                user.setBio(request.getBio());
            }

            if(profilePic != null){
                Optional<List<Media>> existingProfilePic = mediaService.findByContentIdAndType(userId, ContentType.USER_PROFILE);
                existingProfilePic.ifPresent(mediaService::deleteMedia);

                List<Media> profileMedias = mediaService.processAndSaveMediaFiles(new ArrayList<>(Arrays.asList(profilePic)), user.getUserId(), ContentType.USER_PROFILE);
                user.setProfilePic(profileMedias.get(0).getMediaUrl());
            }

            userRepository.save(user);

            return DTOMapper.mapToUserProfileDTO(user);
        } catch(Exception ex){
            System.out.println("Exception - " +  ex);
            // Rollback transaction if media upload fails
            throw new RuntimeException(ex);
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(GenericConstants.USER_NOT_FOUND));

        // Retrieve any associated profile media
        // Delete media files from Cloudinary and database
        Optional<List<Media>> userMedia = mediaService.findByContentIdAndType(userId, ContentType.USER_PROFILE);
        userMedia.ifPresent(mediaService::deleteMedia);

        // Finally, delete the user from the database
        userRepository.delete(user);
    }
}