package com.kbslblog_api.dto.career;

import lombok.Data;

@Data
public class CareerDto {
    private int id;
    private String title;
    private String startDate;
    private String endDate;
}