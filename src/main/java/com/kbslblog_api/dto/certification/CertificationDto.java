package com.kbslblog_api.dto.certification;

import lombok.Data;

@Data
public class CertificationDto {
    private int id;
    private String title;
    private String subTitle;
    private String date;
    private String url;
}