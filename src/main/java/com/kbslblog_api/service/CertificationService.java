package com.kbslblog_api.service;

import com.kbslblog_api.dto.certification.CertificationDto;
import com.kbslblog_api.entity.Certification;
import com.kbslblog_api.mapper.CertificationMapper;
import com.kbslblog_api.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final CertificationMapper certificationMapper;

    public void saveCertification(CertificationDto dto) {
        Certification certification = certificationMapper.toEntity(dto);
        certificationRepository.save(certification);
    }

    public List<CertificationDto> getAllCertifications() {
        List<Certification> certifications = certificationRepository.findAll();

        return certifications.stream()
                .map(certificationMapper::toDto)
                .collect(Collectors.toList());
    }
}