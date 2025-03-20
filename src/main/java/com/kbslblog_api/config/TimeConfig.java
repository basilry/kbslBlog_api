package com.kbslblog_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class TimeConfig {

    public static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(SEOUL_ZONE_ID);

    @Bean
    public Clock clock() {
        return Clock.system(SEOUL_ZONE_ID);
    }
} 