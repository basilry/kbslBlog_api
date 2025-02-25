package com.kbslblog_api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {

    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}
