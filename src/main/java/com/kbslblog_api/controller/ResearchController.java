package com.kbslblog_api.controller;

import com.kbslblog_api.dto.common.ApiResult;
import com.kbslblog_api.dto.research.ResearchDto;
import com.kbslblog_api.service.ResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/research")
@RequiredArgsConstructor
public class ResearchController {

    private final ResearchService researchService;

    @PostMapping
    public ResponseEntity<ApiResult> saveResearch(@RequestBody ResearchDto researchDto) {
        ApiResult result = new ApiResult();

        researchService.saveResearch(researchDto);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping
    public ResponseEntity<ApiResult> getResearchList() {
        ApiResult result = new ApiResult();

        result.setData(researchService.getAllResearches());

        return ResponseEntity.ok().body(result);
    }
}