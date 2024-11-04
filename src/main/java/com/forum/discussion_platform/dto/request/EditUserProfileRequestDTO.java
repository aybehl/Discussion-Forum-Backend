package com.forum.discussion_platform.dto.request;

import lombok.Data;

@Data
public class EditUserProfileRequestDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String bio;
}

