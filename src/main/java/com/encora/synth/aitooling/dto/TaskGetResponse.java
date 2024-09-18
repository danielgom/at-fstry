package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskGetResponse {

    @Schema(example = "aaaabbbbbcccc")
    private String id;

    @NotBlank(message = "Name is required")
    @Schema(example = "my super task")
    private String title;

    @JsonProperty("description")
    @Schema(example = "this task is for...")
    private String description;

    @JsonProperty("due_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2022-11-03T23:49:59.524096")
    private LocalDateTime dueDate;

    @Schema(example = "PENDING, IN_PROGRESS, COMPLETED, NONE")
    private TaskStatus status;
}
