package com.kbslblog_api.controller;


import com.kbslblog_api.dto.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/healthCheck")
public class HealthCheckController {

    /**
     * Handles HTTP GET requests for application health checks.
     *
     * <p>This method creates an ApiResult with its data set to "healthCheck" and returns it wrapped in a ResponseEntity
     * with an HTTP status of 200 OK.</p>
     *
     * @return a ResponseEntity containing the health check ApiResult with an HTTP 200 OK status.
     */
    @GetMapping
    public ResponseEntity<ApiResult> healthCheck() {
        ApiResult result = new ApiResult();

        result.setData("healthCheck");

        return ResponseEntity.ok().body(result);
    }
}