package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.certification.CertificationDto;
import com.kbslblog_api.entity.Certification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CertificationMapper {
    Certification toEntity(CertificationDto dto);
    CertificationDto toDto(Certification entity);
}