package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tsu_taskgraph.core_api.dto.task.TimeLogDto;
import ru.tsu_taskgraph.core_api.entity.TimeLog;


import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeLogMapper {

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.displayName", target = "userDisplayName")
    TimeLogDto toDto(TimeLog timeLog);

    List<TimeLogDto> toDtoList(List<TimeLog> timeLogs);
}
