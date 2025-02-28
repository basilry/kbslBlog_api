package com.kbslblog_api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private String description;
    private String profileImg;

    public UserDto(String loginId, String name, String email, String phoneNumber, String role, String description, String profileImg) {
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.description = description;
        this.profileImg = profileImg;
    }
}
