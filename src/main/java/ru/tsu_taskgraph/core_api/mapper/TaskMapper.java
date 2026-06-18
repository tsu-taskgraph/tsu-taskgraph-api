package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tsu_taskgraph.core_api.dto.task.AssigneeDto;
import ru.tsu_taskgraph.core_api.dto.task.TaskNode;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignees", target = "assignees")
    TaskNode toNode(Task task);

    List<TaskNode> toNodeList(List<Task> tasks);

    @Mapping(source = "id", target = "userId")
    AssigneeDto userToAssigneeDto(User user);
}
