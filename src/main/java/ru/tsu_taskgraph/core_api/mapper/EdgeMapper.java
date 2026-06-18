package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tsu_taskgraph.core_api.dto.project.EdgeResponse;
import ru.tsu_taskgraph.core_api.entity.Edge;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EdgeMapper {

    @Mapping(source = "sourceTask.id", target = "sourceTaskId")
    @Mapping(source = "targetTask.id", target = "targetTaskId")
    EdgeResponse toDto(Edge edge);

    List<EdgeResponse> toDtoList(List<Edge> edges);
}
