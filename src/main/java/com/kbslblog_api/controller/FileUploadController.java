package com.kbslblog_api.controller;

import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


@RestController
@RequiredArgsConstructor
@RequestMapping( "/file")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
        java.io.File convFile = new java.io.File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        return convFile;
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResult> uploadSingleFile(@RequestParam("file") MultipartFile file) throws Exception {
        long startTime = System.currentTimeMillis();
        
        ApiResult result = new ApiResult();
        
        // 임시 파일 생성 없이 직접 MultipartFile 처리
        String fileUrl = fileUploadService.uploadFileDirectly(file);
        System.out.println("컨트롤러에서 받은 파일 URL: " + fileUrl);
        
        result.setData(Collections.singletonMap("fileUrl", fileUrl));
        
        long endTime = System.currentTimeMillis();
        System.out.println("파일 업로드 총 소요 시간: " + (endTime - startTime) + "ms");
        
        // 명시적으로 응답 헤더 설정
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }

    @PostMapping("/multi")
    public ResponseEntity<ApiResult> uploadMultipleFiles(@RequestParam("images") List<MultipartFile> files) throws Exception {
        long startTime = System.currentTimeMillis();
        ApiResult result = new ApiResult();
        
        List<String> uploadedUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            // 임시 파일 생성 없이 직접 MultipartFile 처리
            String fileUrl = fileUploadService.uploadFileDirectly(file);
            uploadedUrls.add(fileUrl);
        }
        
        result.setData(uploadedUrls);
        
        long endTime = System.currentTimeMillis();
        System.out.println("다중 파일 업로드 총 소요 시간: " + (endTime - startTime) + "ms");
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }
    
    // 프론트엔드에서 사용하는 엔드포인트 이름으로 변경
    @PostMapping("/base64-multi")
    public ResponseEntity<ApiResult> uploadBase64MultiImages(@RequestBody Map<String, List<String>> payload) throws Exception {
        long startTime = System.currentTimeMillis();
        ApiResult result = new ApiResult();

        List<String> base64Images = payload.get("images");
        if (base64Images == null || base64Images.isEmpty()) {
            throw new Exception("이미지 데이터가 없습니다.");
        }
        
        // Base64 문자열에서 데이터 부분만 추출 (data:image/jpeg;base64, 부분 제거)
        List<String> processedImages = new ArrayList<>();
        for (String base64Image : base64Images) {
            if (base64Image.contains("base64,")) {
                processedImages.add(base64Image.split("base64,")[1]);
            } else {
                processedImages.add(base64Image);
            }
        }
        
        List<String> urls = fileUploadService.uploadMultiFile(processedImages);
        
        // 프론트엔드에서 기대하는 형식으로 응답 반환 (배열 직접 반환)
        result.setData(urls);
        
        long endTime = System.currentTimeMillis();
        System.out.println("Base64 다중 이미지 업로드 총 소요 시간: " + (endTime - startTime) + "ms");
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }
    
    // 외부 URL 처리 엔드포인트 추가
    @PostMapping("/external-urls")
    public ResponseEntity<ApiResult> processExternalUrls(@RequestBody Map<String, List<String>> payload) throws Exception {
        long startTime = System.currentTimeMillis();
        ApiResult result = new ApiResult();
        
        List<String> externalUrls = payload.get("urls");
        if (externalUrls == null || externalUrls.isEmpty()) {
            throw new Exception("외부 URL 데이터가 없습니다.");
        }
        
        // 외부 URL의 이미지를 다운로드하여 Google Drive에 업로드
        List<String> uploadedUrls = fileUploadService.uploadExternalImages(externalUrls);
        
        result.setData(uploadedUrls);
        
        long endTime = System.currentTimeMillis();
        System.out.println("외부 URL 처리 총 소요 시간: " + (endTime - startTime) + "ms");
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }
    
    // 기존 multi-base64 엔드포인트는 호환성을 위해 유지
    @PostMapping("/multi-base64")
    public ResponseEntity<ApiResult> uploadBase64Images(@RequestBody Map<String, List<String>> payload) throws Exception {
        ApiResult result = new ApiResult();

        List<String> base64Images = payload.get("base64Images");
        List<String> urls = fileUploadService.uploadMultiFile(base64Images);

        result.setData(Collections.singletonMap("urls", urls));

        return ResponseEntity.ok(result);
    }
}
