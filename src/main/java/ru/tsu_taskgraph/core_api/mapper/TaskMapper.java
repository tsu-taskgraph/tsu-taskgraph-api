package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.*;
import ru.tsu_taskgraph.core_api.dto.task.AssigneeDto;
import ru.tsu_taskgraph.core_api.dto.task.TaskNode;
import ru.tsu_taskgraph.core_api.dto.task.UpdateTaskRequest;
import ru.tsu_taskgraph.core_api.entity.Task;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignees", target = "assignees")
    @Mapping(target = "layer", expression = "java(layers.get(task.getId()))")
    TaskNode toNode(Task task, @Context Map<UUID, Integer> layers);

    List<TaskNode> toNodeList(List<Task> tasks, @Context Map<UUID, Integer> layers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "loggedHours", ignore = true)
    @Mapping(target = "wikiPageId", ignore = true)
    @Mapping(target = "enrichment", ignore = true)
    @Mapping(target = "assignees", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(UpdateTaskRequest request, @MappingTarget Task task);
}
