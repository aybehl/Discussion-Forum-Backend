package com.forum.discussion_platform.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.forum.discussion_platform.constants.GenericConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file){
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException(GenericConstants.MEDIA_UPLOAD_ERROR, e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            // Extract public ID from URL to delete the file
            String publicId = extractPublicIdFromUrl(fileUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(GenericConstants.MEDIA_DELETE_ERROR, e);
        }
    }

    private String extractPublicIdFromUrl(String fileUrl) {
        // Cloudinary URL format: "https://res.cloudinary.com/{cloud_name}/image/upload/{public_id}.{extension}"
        // Assuming public_id is the part after the last "/" and before the file extension
        String[] parts = fileUrl.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.substring(0, publicIdWithExtension.lastIndexOf('.'));
    }
}
