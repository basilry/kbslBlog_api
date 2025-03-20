package com.kbslblog_api.controller;

import com.kbslblog_api.entity.ImageFile;
import com.kbslblog_api.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy")
public class ImageProxyController {

    private final ImageFileRepository imageFileRepository;
    private final RestTemplate restTemplate;

    /**
     * Google Drive 이미지를 프록시하여 CORS 문제를 해결합니다.
     * 
     * @param fileId Google Drive 파일 ID
     * @return 이미지 데이터
     */
    @GetMapping(value = "/image/{fileId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> proxyImage(@PathVariable(name = "fileId") String fileId) {
        try {
            // 구글 드라이브 URL 형식 (FileUploadService와 일치)
            String googleDriveUrl = "https://drive.google.com/uc?id=" + fileId;
            
            // fileId로 이미지 파일 URL 찾기
            Optional<ImageFile> imageFile = imageFileRepository.findByUrl(googleDriveUrl);
            
            // 이미지 파일이 존재하지 않으면 404 반환
            if (imageFile.isEmpty()) {
                log.warn("Image not found for fileId: {}", fileId);
                return ResponseEntity.notFound().build();
            }

            // Google Drive에서 이미지 데이터 가져오기
            URI googleDriveUri = new URI(googleDriveUrl + "&export=download");
            
            log.debug("Fetching image from Google Drive: {}", googleDriveUri);
            
            RequestCallback requestCallback = request -> request.getHeaders()
                    .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

            ResponseExtractor<ResponseEntity<byte[]>> responseExtractor = response -> {
                HttpHeaders headers = new HttpHeaders();
                
                // Content-Type 헤더 설정
                String contentType = imageFile.get().getMimeType();
                if (contentType == null || contentType.isEmpty()) {
                    contentType = response.getHeaders().getContentType().toString();
                }
                headers.setContentType(MediaType.parseMediaType(contentType));
                
                // 캐싱 헤더 설정 (1주일 동안 캐싱)
                headers.setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());
                
                // 이미지 데이터 설정
                byte[] imageData = StreamUtils.copyToByteArray(response.getBody());
                log.debug("Image fetched successfully, size: {} bytes", imageData.length);
                return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            };

            // 실제 요청 실행
            try {
                return restTemplate.execute(googleDriveUri, HttpMethod.GET, requestCallback, responseExtractor);
            } catch (Exception e) {
                log.error("Error fetching image from Google Drive: {}", e.getMessage(), e);
                
                // 이미지를 가져오는 데 실패한 경우 기본 에러 이미지를 반환하는 대신
                // 직접 Google Drive URL로 리다이렉트
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(googleDriveUri);
                return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
            }
        } catch (Exception e) {
            log.error("Error proxying image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 이미지 URL로 이미지 프록시
     * 
     * @param imageUrl Google Drive 이미지 URL (인코딩된 형태)
     * @return 이미지 데이터
     */
    @GetMapping(value = "/url", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> proxyImageByUrl(@RequestParam(name = "imageUrl") String imageUrl) {
        try {
            String decodedUrl = java.net.URLDecoder.decode(imageUrl, "UTF-8");
            log.info("Proxying image by URL: {}", decodedUrl);
            
            // Google Drive ID 추출
            Pattern pattern = Pattern.compile("id=([a-zA-Z0-9_-]+)");
            Matcher matcher = pattern.matcher(decodedUrl);
            
            if (matcher.find()) {
                String fileId = matcher.group(1);
                return proxyImage(fileId);
            } else {
                log.warn("Invalid Google Drive URL format: {}", decodedUrl);
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error proxying image by URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 