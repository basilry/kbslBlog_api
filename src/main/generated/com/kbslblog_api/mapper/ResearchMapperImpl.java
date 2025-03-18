package com.kbslblog_api.mapper;

import com.kbslblog_api.dto.research.ResearchDto;
import com.kbslblog_api.entity.Research;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-14T15:06:58+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class ResearchMapperImpl implements ResearchMapper {

    @Override
    public Research toEntity(ResearchDto dto) {
        if ( dto == null ) {
            return null;
        }

        Research.ResearchBuilder research = Research.builder();

        research.id( (long) dto.getId() );
        research.title( dto.getTitle() );
        research.subTitle( dto.getSubTitle() );
        research.date( dto.getDate() );
        research.url( dto.getUrl() );

        return research.build();
    }

    @Override
    public ResearchDto toDto(Research entity) {
        if ( entity == null ) {
            return null;
        }

        ResearchDto researchDto = new ResearchDto();

        if ( entity.getId() != null ) {
            researchDto.setId( entity.getId().intValue() );
        }
        researchDto.setTitle( entity.getTitle() );
        researchDto.setSubTitle( entity.getSubTitle() );
        researchDto.setDate( entity.getDate() );
        researchDto.setUrl( entity.getUrl() );

        return researchDto;
    }
}
