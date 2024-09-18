package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGetResponse {

    @Schema(example = "asdasda12312312sdzxcvbzxcvzxcv")
    private String id;

    @Schema(example = "Jaime")
    private String name;

    @Schema(example = "hernandez")
    @JsonProperty("last_name")
    private String lastName;

    @Schema(example = "anyemail@hotmail.com")
    private String email;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2022-11-03T23:49:59.524096")
    private LocalDateTime createdAt;

}
