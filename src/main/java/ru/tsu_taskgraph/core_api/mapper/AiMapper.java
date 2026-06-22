package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tsu_taskgraph.core_api.dto.ai.*;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.Task;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AiMapper {
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "techStack", source = "project.techStack")
    @Mapping(target = "description", source = "project.description")
    @Mapping(target = "teamSize", source = "project.teamSize")
    @Mapping(target = "aiEstimate", source = "project.aiEstimate")
    @Mapping(target = "providerConfig", source = "providerConfig")
    GenerateSkeletonRequest toGenerateSkeletonRequest(Project project, ProviderConfig providerConfig);

    @Mapping(target = "nodes", source = "tasks")
    @Mapping(target = "edges", source = "edges")
    GraphSnapshot createGraphSnapshot(List<Task> tasks, List<Edge> edges);

    NodeSnapshot taskToNodeSnapshot(Task task);

    @Mapping(target = "sourceTaskId", source = "sourceTask.id")
    @Mapping(target = "targetTaskId", source = "targetTask.id")
    EdgeSnapshot edgeToEdgeSnapshot(Edge edge);
}