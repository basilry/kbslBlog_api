package com.kbslblog_api.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String role;

    private String description;
}
