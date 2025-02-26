package com.kbslblog_api.controller;

import com.kbslblog_api.dto.career.CareerDto;
import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/career")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @PostMapping
    public ResponseEntity<ApiResult> saveCareer(@RequestBody CareerDto careerDto) {
        ApiResult result = new ApiResult();

        careerService.saveCareer(careerDto);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping
    public ResponseEntity<ApiResult> getAllCareer() {
        ApiResult result = new ApiResult();

        result.setData(careerService.getAllCareer());

        return ResponseEntity.ok().body(result);
    }
}
