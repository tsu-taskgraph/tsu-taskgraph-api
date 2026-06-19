package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiMediaDto;
import ru.tsu_taskgraph.core_api.entity.WikiMedia;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WikiMediaMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "uploadedBy.id", target = "uploadedBy")
    WikiMediaDto toDto(WikiMedia media);

    List<WikiMediaDto> toDtoList(List<WikiMedia> mediaList);
}
