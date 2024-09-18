package com.encora.synth.aitooling.controller;

import com.encora.synth.aitooling.dto.*;
import com.encora.synth.aitooling.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Creates a new task.", description = "Registers a new task to the user.")
    @SecurityRequirement(name = "BearerAuthentication")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskCreateResponse> create(@RequestBody TaskCreateRequest request) {
        return new ResponseEntity<>(taskService.create(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Gets all tasks of a user.", description = "Gets the information of all tasks of a user," +
            " can user filtering, ordering and pagination.")
    @SecurityRequirement(name = "BearerAuthentication")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskGetAllResponse> search(@ModelAttribute TaskFilter taskFilter) {
        return new ResponseEntity<>(taskService.search(taskFilter), HttpStatus.OK);
    }

    @Operation(summary = "Gets a task by ID", description = "Gets the information of the task")
    @SecurityRequirement(name = "BearerAuthentication")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskGetResponse> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(taskService.getByID(id));
    }

    @Operation(summary = "Updates a task", description = "Updates the information of a task")
    @SecurityRequirement(name = "BearerAuthentication")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskGetResponse> update(@PathVariable("id") String id,
                                                  @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @Operation(summary = "Deletes a task by id.", description = "Removes a task of the list.")
    @SecurityRequirement(name = "BearerAuthentication")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
