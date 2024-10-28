package com.forum.discussion_platform.service;

import com.forum.discussion_platform.enums.ContentType;
import com.forum.discussion_platform.enums.MediaType;
import com.forum.discussion_platform.model.Media;
import com.forum.discussion_platform.model.Question;
import com.forum.discussion_platform.repository.MediaRepository;
import com.forum.discussion_platform.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public MediaService(MediaRepository mediaRepository, CloudinaryService cloudinaryService) {
        this.mediaRepository = mediaRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Media> processAndSaveMediaFiles(List<MultipartFile> mediaFiles, Long contentId, ContentType contentType) {
        List<Media> mediaList = new ArrayList<>();
        for(MultipartFile file: mediaFiles){
            String mediaUrl = cloudinaryService.uploadFile(file);
            MediaType mediaType = file.getContentType().startsWith("image") ? MediaType.IMAGE: MediaType.VIDEO;
            Media media = Media.builder()
                    .mediaUrl(mediaUrl)
                    .mediaType(mediaType)
                    .contentId(contentId)
                    .contentType(contentType)
                    .build();
            mediaList.add(media);
        }

        return mediaRepository.saveAll(mediaList);
    }

    public List<Media> manageMedia(List<Long> mediaIdsToDelete,
                             List<MultipartFile> newMediaFiles, Long contentId, ContentType contentType) {
        // Delete specified media
        List<Media> mediaToRemove = mediaRepository.findAllById(mediaIdsToDelete);
        mediaRepository.deleteAll(mediaToRemove);

        List<Media> existingMediaList = mediaRepository.findByContentIdAndContentType(contentId, contentType);
        List<Media> newMediaList = processAndSaveMediaFiles(newMediaFiles, contentId, contentType);

        //Merge the two Lists
        return ListUtil.mergeLists(newMediaList, existingMediaList);
    }
}
