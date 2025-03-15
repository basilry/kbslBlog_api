package com.kbslblog_api.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRegisterDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    // 썸네일은 선택사항 (Base64나 URL 형태로 전달)
    private String thumbnail;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
