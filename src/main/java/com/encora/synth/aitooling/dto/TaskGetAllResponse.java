package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskGetAllResponse {

    List<TaskGetResponse> tasks;

    @JsonProperty("total_elements")
    private long totalElements;

    @JsonProperty("total_pages")
    private long totalPages;

}
