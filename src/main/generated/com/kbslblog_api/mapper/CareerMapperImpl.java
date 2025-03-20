package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.career.CareerDto;
import com.kbslblog_api.entity.Career;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-14T15:06:58+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class CareerMapperImpl implements CareerMapper {

    @Override
    public Career toEntity(CareerDto dto) {
        if ( dto == null ) {
            return null;
        }

        Career.CareerBuilder career = Career.builder();

        career.id( (long) dto.getId() );
        career.title( dto.getTitle() );
        career.startDate( dto.getStartDate() );
        career.endDate( dto.getEndDate() );

        return career.build();
    }

    @Override
    public CareerDto toDto(Career entity) {
        if ( entity == null ) {
            return null;
        }

        CareerDto careerDto = new CareerDto();

        if ( entity.getId() != null ) {
            careerDto.setId( entity.getId().intValue() );
        }
        careerDto.setTitle( entity.getTitle() );
        careerDto.setStartDate( entity.getStartDate() );
        careerDto.setEndDate( entity.getEndDate() );

        return careerDto;
    }
}
