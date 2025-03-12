package com.kbslblog_api.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    // 썸네일은 선택사항 (URL 또는 Base64 문자열)
    private String thumbnail;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}