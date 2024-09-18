package com.encora.synth.aitooling.service.impl;

import com.encora.synth.aitooling.dto.*;
import com.encora.synth.aitooling.dto.exception.UserException;
import com.encora.synth.aitooling.mapper.TaskMapper;
import com.encora.synth.aitooling.model.Task;
import com.encora.synth.aitooling.repository.TaskRepository;
import com.encora.synth.aitooling.service.TaskService;
import com.encora.synth.aitooling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final MongoTemplate mongoTemplate;

    @Override
    public TaskCreateResponse create(TaskCreateRequest request) {
        Task savedTask = taskRepository.save(TaskMapper.MAPPER.toTask(request, userService.getUserID()));
        return TaskMapper.MAPPER.toTaskCreateResponse(savedTask);
    }

    @Override
    public TaskGetAllResponse search(TaskFilter taskFilter) {
        if (taskFilter.allNull()) {
            List<TaskGetResponse> taskList = taskRepository.findAllByUserID(userService.getUserID()).stream()
                    .map(TaskMapper.MAPPER::toTaskGetResponse)
                    .toList();
            return TaskGetAllResponse.builder()
                    .tasks(taskList)
                    .totalElements(taskList.size())
                    .totalPages(1)
                    .build();
        }

        taskFilter.setPageNumber(taskFilter.getPageNumber() != null ? taskFilter.getPageNumber() - 1 : 0);
        taskFilter.setPageSize(taskFilter.getPageSize() != null ? taskFilter.getPageSize() : 10);

        Criteria criteria = new Criteria();
        if (taskFilter.getStatus() != null && StringUtils.hasText(taskFilter.getStatus().getStatus())) {
            criteria = criteria.and("status")
                    .is(taskFilter.getStatus().getStatus());
        }

        Query query = new Query();
        if (!criteria.equals(new Criteria())) {
            query.addCriteria(criteria);
        }

        query.addCriteria(criteria.and("user_id").is(userService.getUserID()));

        if (StringUtils.hasText(taskFilter.getOrderBy()) && StringUtils.hasText(taskFilter.getOrderType())) {
            Sort.Direction sortDirection = Sort.Direction.fromString(taskFilter.getOrderType());
            query.with(Sort.by(sortDirection, taskFilter.getOrderBy()));
        }

        long taskCount = mongoTemplate.count(query, Task.class);

        List<TaskGetResponse> taskList = mongoTemplate
                .find(query.with(buildPaging(taskFilter)), Task.class).stream()
                .map(TaskMapper.MAPPER::toTaskGetResponse)
                .toList();

        long totalPages = (long) Math.ceil((double) taskCount / taskFilter.getPageSize());

        return TaskGetAllResponse.builder()
                .totalElements(taskCount)
                .totalPages(totalPages)
                .tasks(taskList)
                .build();
    }

    @Override
    public TaskGetResponse getByID(String taskID) {
        return TaskMapper.MAPPER.toTaskGetResponse(taskRepository.findByIdAndUserID(taskID, userService.getUserID()).orElseThrow(()
                -> new UserException(String.format("task with id (%s) not found for this user", taskID), HttpStatus.NOT_FOUND)));
    }

    @Override
    public TaskGetResponse update(String taskID, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndUserID(taskID, userService.getUserID()).orElseThrow(()
                -> new UserException(String.format("task with id (%s) not found for this user", taskID), HttpStatus.NOT_FOUND));

        boolean toUpdate = false;
        if (StringUtils.hasText(request.getTitle())) {
            toUpdate = true;
            task.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getDescription())) {
            toUpdate = true;
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            toUpdate = true;
            task.setDueDate(request.getDueDate());
        }

        if (request.getStatus() != null) {
            toUpdate = true;
            task.setStatus(request.getStatus().getStatus());
        }

        if (toUpdate) {
            taskRepository.save(task);
        }

        return TaskMapper.MAPPER.toTaskGetResponse(task);
    }

    @Override
    public void deleteById(String taskID) {
        taskRepository.deleteByIdAndUserID(taskID, userService.getUserID());
    }

    private Pageable buildPaging(TaskFilter taskFilter) {
        return PageRequest.of(
                taskFilter.getPageNumber(),
                taskFilter.getPageSize()
        );
    }
}
