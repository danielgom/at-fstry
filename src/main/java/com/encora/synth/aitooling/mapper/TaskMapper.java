package com.encora.synth.aitooling.mapper;

import com.encora.synth.aitooling.dto.TaskCreateRequest;
import com.encora.synth.aitooling.dto.TaskCreateResponse;
import com.encora.synth.aitooling.dto.TaskGetResponse;
import com.encora.synth.aitooling.dto.TaskStatus;
import com.encora.synth.aitooling.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper
public interface TaskMapper {

    TaskMapper MAPPER = Mappers.getMapper(TaskMapper.class);

    @Mapping(source = "userID", target = "userID")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "request.title", target = "title")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "request.dueDate", target = "dueDate")
    @Mapping(source = "request.status", target = "status", qualifiedByName = "taskStatusToString")
    Task toTask(TaskCreateRequest request, String userID);

    @Named("taskStatusToString")
    default String taskStatusToString(TaskStatus status) {
        return Objects.requireNonNullElse(status, TaskStatus.NONE).getStatus();
    }

    @Mapping(source = "task.description", target = "description",
            defaultExpression = "java(java.util.Optional.ofNullable(task.getDescription()).orElse(\"\"))")
    @Mapping(source = "task.status", target = "status", qualifiedByName = "stringToTaskStatus")
    TaskCreateResponse toTaskCreateResponse(Task task);

    @Mapping(source = "task.description", target = "description",
            defaultExpression = "java(java.util.Optional.ofNullable(task.getDescription()).orElse(\"\"))")
    @Mapping(source = "task.status", target = "status", qualifiedByName = "stringToTaskStatus")
    TaskGetResponse toTaskGetResponse(Task task);

    @Named("stringToTaskStatus")
    default TaskStatus stringToTaskStatus(String toTaskStatus) {
        return TaskStatus.fromString(toTaskStatus);
    }
}
