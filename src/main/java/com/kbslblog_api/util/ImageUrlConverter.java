package com.kbslblog_api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 이미지 URL을 프록시 URL로 변환하는 유틸리티 클래스
 */
@Slf4j
@Component
public class ImageUrlConverter {

    // 구글 드라이브 URL 패턴 (id= 뒤의 파일 ID 추출)
    private static final Pattern GOOGLE_DRIVE_URL_PATTERN = Pattern.compile("<img[^>]*src=[\"']https://drive\\.google\\.com/uc\\?id=([a-zA-Z0-9_-]+)[\"'][^>]*>");
    
    // 구글 드라이브 단일 URL 패턴
    private static final Pattern GOOGLE_DRIVE_SINGLE_URL_PATTERN = Pattern.compile("https://drive\\.google\\.com/uc\\?id=([a-zA-Z0-9_-]+)");

    /**
     * HTML 콘텐츠 내의 구글 드라이브 이미지 URL을 프록시 URL로 변환합니다.
     *
     * @param content HTML 콘텐츠
     * @return 프록시 URL로 변환된 HTML 콘텐츠
     */
    public String convertGoogleDriveUrlsToProxyUrls(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        log.debug("원본 콘텐츠 길이: {}", content.length());
        
        // 구글 드라이브 URL을 프록시 URL로 대체
        Matcher matcher = GOOGLE_DRIVE_URL_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String originalImgTag = matcher.group(0);
            String fileId = matcher.group(1);
            String proxyUrl = "/proxy/image/" + fileId;
            
            // 원본 img 태그에서 src 속성만 변경
            String newImgTag = originalImgTag.replaceFirst(
                    "src=[\"']https://drive\\.google\\.com/uc\\?id=[a-zA-Z0-9_-]+[\"']", 
                    "src=\"" + proxyUrl + "\"");
            
            log.debug("변환: {} -> {}", originalImgTag, newImgTag);
            matcher.appendReplacement(result, Matcher.quoteReplacement(newImgTag));
        }
        matcher.appendTail(result);
        
        String convertedContent = result.toString();
        log.debug("변환된 콘텐츠 길이: {}", convertedContent.length());
        
        return convertedContent;
    }
    
    /**
     * 단일 구글 드라이브 URL을 프록시 URL로 변환합니다.
     * 썸네일과 같은 단일 이미지 URL 처리에 사용됩니다.
     *
     * @param url 변환할 URL
     * @return 프록시 URL로 변환된 URL
     */
    public String convertSingleGoogleDriveUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        Matcher matcher = GOOGLE_DRIVE_SINGLE_URL_PATTERN.matcher(url);
        if (matcher.find()) {
            String fileId = matcher.group(1);
            String proxyUrl = "/proxy/image/" + fileId;
            log.debug("단일 URL 변환: {} -> {}", url, proxyUrl);
            return proxyUrl;
        }
        
        return url;
    }
} 