package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.career.CareerDto;
import com.kbslblog_api.entity.Career;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    Career toEntity(CareerDto dto);
    CareerDto toDto(Career entity);
}