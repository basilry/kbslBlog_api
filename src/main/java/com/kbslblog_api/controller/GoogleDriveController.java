package com.kbslblog_api.controller;

import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.service.GoogleDriveService;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping( "/google-drive")
public class GoogleDriveController {

    private final GoogleDriveService googleDriveService;

    private java.io.File convertMultiPartToFile(MultipartFile file) throws IOException {
        java.io.File convFile = new java.io.File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        return convFile;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResult> uploadSingleFile(@RequestParam("file") MultipartFile file) throws Exception {
        ApiResult result = new ApiResult();

        String fileUrl = googleDriveService.uploadFile(convertMultiPartToFile(file));

        result.setData(Collections.singletonMap("fileUrl", fileUrl));

        return ResponseEntity.ok(result);
    }


    @PostMapping("/upload-multi")
    public ResponseEntity<ApiResult> uploadBase64Images(@RequestBody Map<String, List<String>> payload) throws Exception {
        ApiResult result = new ApiResult();

        List<String> base64Images = payload.get("base64Images");
        List<String> urls = googleDriveService.uploadMultiFile(base64Images);

        result.setData(Collections.singletonMap("urls", urls));

        return ResponseEntity.ok(result);
    }
}
