package com.kbslblog_api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthDto {
    private String username;
    private String password;
    private String token;

    public AuthDto(String token) {
        this.token = token;
    }
}
