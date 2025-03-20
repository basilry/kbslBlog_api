package com.kbslblog_api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        // 애플리케이션 시작 시 JVM 기본 시간대를 Asia/Seoul로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("시스템 기본 시간대: " + TimeZone.getDefault().getID());
    }
} 