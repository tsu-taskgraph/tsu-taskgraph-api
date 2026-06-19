package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.*;
import ru.tsu_taskgraph.core_api.dto.wiki.UpdateWikiPageRequest;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiPageDto;
import ru.tsu_taskgraph.core_api.dto.wiki.WikiPageSummaryDto;
import ru.tsu_taskgraph.core_api.entity.WikiPage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WikiMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "task.id", target = "taskId")
    WikiPageDto toDto(WikiPage wikiPage);

    @Mapping(source = "task.id", target = "taskId")
    WikiPageSummaryDto toSummaryDto(WikiPage wikiPage);

    List<WikiPageSummaryDto> toSummaryDtoList(List<WikiPage> wikiPages);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "authorType", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(UpdateWikiPageRequest request, @MappingTarget WikiPage wikiPage);
}
