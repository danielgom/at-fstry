package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateResponse {

    @Schema(example = "John")
    @JsonProperty("name")
    private String name;

    @Schema(example = "John")
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

}
