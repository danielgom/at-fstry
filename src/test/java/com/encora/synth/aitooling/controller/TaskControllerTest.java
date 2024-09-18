package com.encora.synth.aitooling.controller;

import com.encora.synth.aitooling.dto.*;
import com.encora.synth.aitooling.dto.Error;
import com.encora.synth.aitooling.utils.MongoDBContainerTestExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ExtendWith(MongoDBContainerTestExtension.class)
public class TaskControllerTest extends MongoDBContainerTestExtension {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final String TEST_HOST = "http://localhost:";

    private String authToken;

    @BeforeEach
    void settingUser() {
        if (!StringUtils.hasText(authToken)) {
            UserCreateRequest createRequestTest = UserCreateRequest.builder()
                    .name("Daniel")
                    .lastName("Test")
                    .email("test_user@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserCreateResponse userCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/signup",
                    createRequestTest,
                    UserCreateResponse.class);

            Assertions.assertThat(userCreateResponse).isNotNull();

            UserLoginRequest loginRequestTest = UserLoginRequest.builder()
                    .email("test_user@outlook.com")
                    .password("ValidPass1234@")
                    .build();

            UserLoginResponse loginResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/auth/login",
                    loginRequestTest, UserLoginResponse.class);

            Assertions.assertThat(loginResponse).isNotNull();
            Assertions.assertThat(loginResponse.getToken()).isNotBlank();
            authToken = loginResponse.getToken();
        }
    }

    @Nested
    class CreateTask {
        @Test
        void whenValidRequest_shouldReturnCreatedTask() {
            TaskCreateRequest createTaskRequest = TaskCreateRequest.builder()
                    .title("My super task")
                    .description("This is a great task")
                    .dueDate(LocalDateTime.parse("2024-11-15T17:00:00"))
                    .status(TaskStatus.IN_PROGRESS)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(createTaskRequest, headers);

            TaskCreateResponse taskCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/tasks",
                    request, TaskCreateResponse.class);

            Assertions.assertThat(taskCreateResponse).isNotNull();
            Assertions.assertThat(taskCreateResponse.getId()).isNotBlank();
            Assertions.assertThat(taskCreateResponse.getTitle()).isEqualTo("My super task");
            Assertions.assertThat(taskCreateResponse.getDescription()).isEqualTo("This is a great task");
            Assertions.assertThat(taskCreateResponse.getDueDate()).isEqualTo(LocalDateTime.parse("2024-11-15T17:00:00"));
            Assertions.assertThat(taskCreateResponse.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }
    }

    @Nested
    class GetTask {
        @Test
        void whenValidRequestID_shouldGetTask() {
            TaskCreateRequest createTaskRequest = TaskCreateRequest.builder()
                    .title("My super task in get")
                    .description("This is a great task in get")
                    .dueDate(LocalDateTime.parse("2024-10-10T17:00:00"))
                    .status(TaskStatus.IN_PROGRESS)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(createTaskRequest, headers);

            TaskCreateResponse taskCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/tasks",
                    request, TaskCreateResponse.class);
            Assertions.assertThat(taskCreateResponse).isNotNull();

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<TaskGetResponse> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + taskCreateResponse.getId(),
                    HttpMethod.GET, getRequest,
                    TaskGetResponse.class);

            TaskGetResponse taskGetResponse = getResponseEntity.getBody();
            Assertions.assertThat(taskGetResponse).isNotNull();
            Assertions.assertThat(taskGetResponse.getId()).isEqualTo(taskCreateResponse.getId());
            Assertions.assertThat(taskGetResponse.getTitle()).isEqualTo(taskCreateResponse.getTitle());
            Assertions.assertThat(taskGetResponse.getDescription()).isEqualTo(taskCreateResponse.getDescription());
            Assertions.assertThat(taskGetResponse.getDueDate()).isEqualTo(taskCreateResponse.getDueDate());
            Assertions.assertThat(taskGetResponse.getStatus()).isEqualTo(taskCreateResponse.getStatus());
        }

        @Test
        void whenInvalidRequestID_shouldThrowException() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<Error> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + "randomid1234567",
                    HttpMethod.GET, getRequest,
                    Error.class);

            Error errException = getResponseEntity.getBody();
            Assertions.assertThat(errException).isNotNull();
            Assertions.assertThat(errException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            Assertions.assertThat(errException.getError()).isEqualTo("NOT_FOUND");
            Assertions.assertThat(errException.getReason()).isEqualTo("task with id (randomid1234567) not found for this user");
            Assertions.assertThat(errException.getTimestamp()).isCloseTo(LocalDateTime.now(),
                    Assertions.within(5, ChronoUnit.SECONDS));
            Assertions.assertThat(errException.getPath()).isEqualTo("/api/tasks/randomid1234567");
        }
    }

    @Nested
    class searchTask {

        @Test
        void whenValidRequest_shouldSearchTasks() {
            createTestTask("Task 1", TaskStatus.PENDING);
            createTestTask("Special Task", TaskStatus.IN_PROGRESS);
            createTestTask("Task 2", TaskStatus.COMPLETED);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(null, headers);

            String url = String.format("%s%s/api/tasks",
                    TEST_HOST, port);

            ResponseEntity<TaskGetAllResponse> response = testRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    TaskGetAllResponse.class
            );

            TaskGetAllResponse taskGetAllResponse = response.getBody();
            Assertions.assertThat(taskGetAllResponse).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotEmpty();
            Assertions.assertThat(taskGetAllResponse.getTotalElements()).isEqualTo(9);
            Assertions.assertThat(taskGetAllResponse.getTotalPages()).isEqualTo(1);
        }

        @Test
        void whenPaginated_shouldSearchTasksByPages() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(null, headers);

            int pageNumber = 1;
            int pageSize = 5;

            String url = String.format("%s%s/api/tasks?pageNumber=%d&pageSize=%d",
                    TEST_HOST, port, pageNumber, pageSize);

            ResponseEntity<TaskGetAllResponse> response = testRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    TaskGetAllResponse.class
            );

            TaskGetAllResponse taskGetAllResponse = response.getBody();
            Assertions.assertThat(taskGetAllResponse).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotEmpty();
            Assertions.assertThat(taskGetAllResponse.getTasks().size()).isEqualTo(pageSize);
            Assertions.assertThat(taskGetAllResponse.getTotalElements()).isEqualTo(9);
            Assertions.assertThat(taskGetAllResponse.getTotalPages()).isEqualTo(2);
        }

        @Test
        void whenFilteredByStatus_shouldSearchTasksByStatus() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(null, headers);

            String status = TaskStatus.PENDING.name();

            String url = String.format("%s%s/api/tasks?status=%s",
                    TEST_HOST, port, status);

            ResponseEntity<TaskGetAllResponse> response = testRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    TaskGetAllResponse.class
            );

            TaskGetAllResponse taskGetAllResponse = response.getBody();
            Assertions.assertThat(taskGetAllResponse).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotEmpty();
            Assertions.assertThat(taskGetAllResponse.getTotalElements()).isEqualTo(3);
            Assertions.assertThat(taskGetAllResponse.getTotalPages()).isEqualTo(1);
        }

        @Test
        void whenOrderedByDueDateDesc_shouldSearchTasksByOrderDueDate() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(null, headers);

            String orderBy = "due_date";
            String orderType = "DESC";

            String url = String.format("%s%s/api/tasks?orderBy=%s&orderType=%s",
                    TEST_HOST, port, orderBy, orderType);

            ResponseEntity<TaskGetAllResponse> response = testRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    TaskGetAllResponse.class
            );

            TaskGetAllResponse taskGetAllResponse = response.getBody();
            Assertions.assertThat(taskGetAllResponse).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotNull();
            Assertions.assertThat(taskGetAllResponse.getTasks()).isNotEmpty();
            Assertions.assertThat(taskGetAllResponse.getTotalElements()).isEqualTo(6);
            Assertions.assertThat(taskGetAllResponse.getTotalPages()).isEqualTo(1);
        }

        private void createTestTask(String title, TaskStatus status) {
            TaskCreateRequest createTaskRequest = TaskCreateRequest.builder()
                    .title(title)
                    .description("Test description")
                    .dueDate(LocalDateTime.now().plusDays(1))
                    .status(status)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(createTaskRequest, headers);

            testRestTemplate.postForObject(TEST_HOST + port + "/api/tasks", request, TaskCreateResponse.class);
        }
    }

    @Nested
    class UpdateTask {

        private String createdTaskID;

        @BeforeEach
        void settingTaskToUpdate() {
            if (!StringUtils.hasText(createdTaskID)) {
                TaskCreateRequest createTaskRequest = TaskCreateRequest.builder()
                        .title("My super task to update")
                        .description("This is a great task to update")
                        .dueDate(LocalDateTime.parse("2024-10-10T17:00:00"))
                        .status(TaskStatus.PENDING)
                        .build();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", String.format("Bearer %s", authToken));
                HttpEntity<TaskCreateRequest> request = new HttpEntity<>(createTaskRequest, headers);

                TaskCreateResponse taskCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/tasks",
                        request, TaskCreateResponse.class);

                Assertions.assertThat(taskCreateResponse).isNotNull();
                createdTaskID = taskCreateResponse.getId();
            }
        }

        @Test
        void whenTitle_shouldUpdateTaskTitle() {
            TaskUpdateRequest updateTaskRequest = TaskUpdateRequest.builder()
                    .title("Updated title for my super task")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskUpdateRequest> request = new HttpEntity<>(updateTaskRequest, headers);

            testRestTemplate.put(TEST_HOST + port + "/api/tasks/" + createdTaskID, request, TaskGetResponse.class);

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<TaskGetResponse> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + createdTaskID,
                    HttpMethod.GET, getRequest,
                    TaskGetResponse.class);

            TaskGetResponse taskGetResponse = getResponseEntity.getBody();
            Assertions.assertThat(taskGetResponse).isNotNull();
            Assertions.assertThat(taskGetResponse.getId()).isEqualTo(createdTaskID);
            Assertions.assertThat(taskGetResponse.getTitle()).isEqualTo("Updated title for my super task");
            Assertions.assertThat(taskGetResponse.getDescription()).isEqualTo("This is a great task to update");
            Assertions.assertThat(taskGetResponse.getDueDate()).isEqualTo(LocalDateTime.parse("2024-10-10T17:00:00"));
            Assertions.assertThat(taskGetResponse.getStatus()).isEqualTo(TaskStatus.PENDING);
        }

        @Test
        void whenDescription_shouldUpdateTaskDescription() {
            TaskUpdateRequest updateTaskRequest = TaskUpdateRequest.builder()
                    .description("This is my updated description for this update task")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskUpdateRequest> request = new HttpEntity<>(updateTaskRequest, headers);

            testRestTemplate.put(TEST_HOST + port + "/api/tasks/" + createdTaskID, request, TaskGetResponse.class);

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<TaskGetResponse> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + createdTaskID,
                    HttpMethod.GET, getRequest,
                    TaskGetResponse.class);

            TaskGetResponse taskGetResponse = getResponseEntity.getBody();
            Assertions.assertThat(taskGetResponse).isNotNull();
            Assertions.assertThat(taskGetResponse.getId()).isEqualTo(createdTaskID);
            Assertions.assertThat(taskGetResponse.getDescription()).isEqualTo("This is my updated description for this update task");
        }

        @Test
        void whenStatus_shouldUpdateTaskStatus() {
            TaskUpdateRequest updateTaskRequest = TaskUpdateRequest.builder()
                    .status(TaskStatus.IN_PROGRESS)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskUpdateRequest> request = new HttpEntity<>(updateTaskRequest, headers);

            testRestTemplate.put(TEST_HOST + port + "/api/tasks/" + createdTaskID, request, TaskGetResponse.class);

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<TaskGetResponse> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + createdTaskID,
                    HttpMethod.GET, getRequest,
                    TaskGetResponse.class);

            TaskGetResponse taskGetResponse = getResponseEntity.getBody();
            Assertions.assertThat(taskGetResponse).isNotNull();
            Assertions.assertThat(taskGetResponse.getId()).isEqualTo(createdTaskID);
            Assertions.assertThat(taskGetResponse.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }

        @Test
        void whenDueDate_shouldUpdateTaskDueDate() {
            TaskUpdateRequest updateTaskRequest = TaskUpdateRequest.builder()
                    .dueDate(LocalDateTime.parse("2025-11-15T09:00:00"))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskUpdateRequest> request = new HttpEntity<>(updateTaskRequest, headers);

            testRestTemplate.put(TEST_HOST + port + "/api/tasks/" + createdTaskID, request, TaskGetResponse.class);

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<TaskGetResponse> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + createdTaskID,
                    HttpMethod.GET, getRequest,
                    TaskGetResponse.class);

            TaskGetResponse taskGetResponse = getResponseEntity.getBody();
            Assertions.assertThat(taskGetResponse).isNotNull();
            Assertions.assertThat(taskGetResponse.getId()).isEqualTo(createdTaskID);
            Assertions.assertThat(taskGetResponse.getDueDate()).isEqualTo(LocalDateTime.parse("2025-11-15T09:00:00"));
        }

        @Test
        void whenFullUpdate_shouldUpdateAllTaskInfo() {
            TaskUpdateRequest updateTaskRequest = TaskUpdateRequest.builder()
                    .title("Updated title full")
                    .description("Full description of the tast")
                    .dueDate(LocalDateTime.parse("2026-01-02T10:00:00"))
                    .status(TaskStatus.COMPLETED)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskUpdateRequest> request = new HttpEntity<>(updateTaskRequest, headers);

            testRestTemplate.put(TEST_HOST + port + "/api/tasks/" + createdTaskID, request, TaskGetResponse.class);

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            ResponseEntity<TaskGetResponse> getResponseEntity = testRestTemplate.exchange(TEST_HOST + port + "/api/tasks/" + createdTaskID,
                    HttpMethod.GET, getRequest,
                    TaskGetResponse.class);

            TaskGetResponse taskGetResponse = getResponseEntity.getBody();
            Assertions.assertThat(taskGetResponse).isNotNull();
            Assertions.assertThat(taskGetResponse.getId()).isEqualTo(createdTaskID);
            Assertions.assertThat(taskGetResponse.getDueDate()).isEqualTo(LocalDateTime.parse("2026-01-02T10:00:00"));
            Assertions.assertThat(taskGetResponse.getTitle()).isEqualTo("Updated title full");
            Assertions.assertThat(taskGetResponse.getDescription()).isEqualTo("Full description of the tast");
            Assertions.assertThat(taskGetResponse.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        }
    }

    @Nested
    class DeleteTask {
        @Test
        void whenValidRequestID_shouldDeleteTask() {
            TaskCreateRequest createTaskRequest = TaskCreateRequest.builder()
                    .title("My super task to delete")
                    .description("This is a great task to delete")
                    .dueDate(LocalDateTime.parse("2024-10-10T17:00:00"))
                    .status(TaskStatus.COMPLETED)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<TaskCreateRequest> request = new HttpEntity<>(createTaskRequest, headers);

            TaskCreateResponse taskCreateResponse = testRestTemplate.postForObject(TEST_HOST + port + "/api/tasks",
                    request, TaskCreateResponse.class);
            Assertions.assertThat(taskCreateResponse).isNotNull();

            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            testRestTemplate.delete(TEST_HOST + port + "/api/tasks/" + taskCreateResponse.getId(),
                    getRequest,
                    Void.class);
        }

        @Test
        void whenInvalidID_shouldDoNothing() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", String.format("Bearer %s", authToken));
            HttpEntity<Void> getRequest = new HttpEntity<>(null, headers);
            testRestTemplate.delete(TEST_HOST + port + "/api/tasks/" + "adfasdflxjkblxcjkvb",
                    getRequest,
                    Void.class);
        }
    }
}
