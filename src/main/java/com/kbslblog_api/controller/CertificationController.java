package com.kbslblog_api.controller;

import com.kbslblog_api.dto.certification.CertificationDto;
import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certification")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public ResponseEntity<ApiResult> saveCertification(@RequestBody CertificationDto certificationDto) {
        ApiResult result = new ApiResult();

        certificationService.saveCertification(certificationDto);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping
    public ResponseEntity<ApiResult> getAllCertifications() {
        ApiResult result = new ApiResult();

        List<CertificationDto> certifications = certificationService.getAllCertifications();
        result.setData(certifications);

        return ResponseEntity.ok().body(result);
    }
}