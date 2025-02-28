package com.kbslblog_api.dto.research;

import lombok.Data;

@Data
public class ResearchDto {
    private int id;
    private String title;
    private String subTitle;
    private String date;
    private String url;
}