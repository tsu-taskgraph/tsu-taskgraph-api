package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.*;
import ru.tsu_taskgraph.core_api.dto.task.TaskNode;
import ru.tsu_taskgraph.core_api.dto.task.UpdateTaskRequest;
import ru.tsu_taskgraph.core_api.entity.Task;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignees", target = "assignees")
    TaskNode toNode(Task task);

    List<TaskNode> toNodeList(List<Task> tasks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UpdateTaskRequest request, @MappingTarget Task task);
}
