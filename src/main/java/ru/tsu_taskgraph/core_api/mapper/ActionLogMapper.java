package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tsu_taskgraph.core_api.dto.log.ActionLogEntryDto;
import ru.tsu_taskgraph.core_api.entity.ActionLogEntry;

@Mapper(componentModel = "spring")
public interface ActionLogMapper {

    @Mapping(source = "project.id", target = "projectId")
    ActionLogEntryDto toDto(ActionLogEntry entry);
}
