package com.kbslblog_api.controller;


import com.kbslblog_api.dto.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/healthCheck")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<ApiResult> healthCheck() {
        ApiResult result = new ApiResult();

        result.setData("healthCheck");

        return ResponseEntity.ok().body(result);
    }
}