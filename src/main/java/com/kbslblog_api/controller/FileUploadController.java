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
        
        String fileUrl = fileUploadService.uploadFile(convertMultiPartToFile(file));
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
    public ResponseEntity<ApiResult> uploadBase64Images(@RequestBody Map<String, List<String>> payload) throws Exception {
        ApiResult result = new ApiResult();

        List<String> base64Images = payload.get("base64Images");
        List<String> urls = fileUploadService.uploadMultiFile(base64Images);

        result.setData(Collections.singletonMap("urls", urls));

        return ResponseEntity.ok(result);
    }
}
