package com.kbslblog_api.util;

import com.kbslblog_api.config.TimeConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜와 시간 처리를 위한 유틸리티 클래스
 */
public class DateTimeUtil {
    
    /**
     * 서울 시간대 기준으로 현재 시간을 반환합니다.
     * @return 서울 시간대의 현재 LocalDateTime
     */
    public static LocalDateTime nowInSeoul() {
        return LocalDateTime.now(TimeConfig.SEOUL_ZONE_ID);
    }
    
    /**
     * LocalDateTime을 기본 포맷(yyyy-MM-dd HH:mm:ss)으로 문자열 변환
     * @param dateTime 변환할 LocalDateTime
     * @return 포맷된 문자열
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return TimeConfig.DEFAULT_DATETIME_FORMATTER.format(dateTime);
    }
    
    /**
     * 문자열을 LocalDateTime으로 파싱
     * @param dateTimeStr 파싱할 문자열 (yyyy-MM-dd HH:mm:ss 형식)
     * @return 파싱된 LocalDateTime
     */
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, TimeConfig.DEFAULT_DATETIME_FORMATTER);
    }
} 