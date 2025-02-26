package com.kbslblog_api.service;

import com.kbslblog_api.dto.research.ResearchDto;
import com.kbslblog_api.entity.Research;
import com.kbslblog_api.mapper.ResearchMapper;
import com.kbslblog_api.repository.ResearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ResearchService {

    private final ResearchRepository researchRepository;
    private final ResearchMapper researchMapper;

    public void saveResearch(ResearchDto dto) {
        Research research = researchMapper.toEntity(dto);
        researchRepository.save(research);
    }

    public List<ResearchDto> getAllResearches() {
        List<Research> list = researchRepository.findAll();
        return list.stream()
                .map(researchMapper::toDto)
                .collect(Collectors.toList());
    }
}