package com.kbslblog_api.dto.user;

import lombok.Data;

@Data
public class UserProfileUpdateDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String description;
    private String profileImg;
}