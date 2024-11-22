package com.forum.discussion_platform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditQuestionRequestDTO {
    private String title;
    private String body;
    private List<Long> tagsToDelete;
    private List<Long> newTagIds;
    private List<Long> mediaToDelete;
}
