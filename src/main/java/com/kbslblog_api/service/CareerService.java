package com.kbslblog_api.service;

import com.kbslblog_api.dto.career.CareerDto;
import com.kbslblog_api.entity.Career;
import com.kbslblog_api.mapper.CareerMapper;
import com.kbslblog_api.repository.CareerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;

    public void saveCareer(CareerDto dto) {
        Career career = careerMapper.toEntity(dto);
        careerRepository.save(career);
    }

    public List<CareerDto> getAllCareer() {
        List<Career> careers = careerRepository.findAll();
        return careers.stream().map(careerMapper::toDto).collect(Collectors.toList());
    }
}
