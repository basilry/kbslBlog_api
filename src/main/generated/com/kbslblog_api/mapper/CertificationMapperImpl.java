package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.certification.CertificationDto;
import com.kbslblog_api.entity.Certification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-14T15:06:58+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class CertificationMapperImpl implements CertificationMapper {

    @Override
    public Certification toEntity(CertificationDto dto) {
        if ( dto == null ) {
            return null;
        }

        Certification.CertificationBuilder certification = Certification.builder();

        certification.id( (long) dto.getId() );
        certification.title( dto.getTitle() );
        certification.subTitle( dto.getSubTitle() );
        certification.date( dto.getDate() );
        certification.url( dto.getUrl() );

        return certification.build();
    }

    @Override
    public CertificationDto toDto(Certification entity) {
        if ( entity == null ) {
            return null;
        }

        CertificationDto certificationDto = new CertificationDto();

        if ( entity.getId() != null ) {
            certificationDto.setId( entity.getId().intValue() );
        }
        certificationDto.setTitle( entity.getTitle() );
        certificationDto.setSubTitle( entity.getSubTitle() );
        certificationDto.setDate( entity.getDate() );
        certificationDto.setUrl( entity.getUrl() );

        return certificationDto;
    }
}
