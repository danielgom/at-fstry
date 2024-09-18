package com.encora.synth.aitooling.service;

import com.encora.synth.aitooling.dto.*;

public interface TaskService {

    TaskCreateResponse create(TaskCreateRequest request);

    TaskGetAllResponse search(TaskFilter taskFilter);

    TaskGetResponse getByID(String taskID);

    TaskGetResponse update(String taskID, TaskUpdateRequest request);

    void deleteById(String taskID);
}
