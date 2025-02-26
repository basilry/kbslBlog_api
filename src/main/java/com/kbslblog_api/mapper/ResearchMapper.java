package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.research.ResearchDto;
import com.kbslblog_api.entity.Research;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResearchMapper {
    Research toEntity(ResearchDto dto);
    ResearchDto toDto(Research entity);
}