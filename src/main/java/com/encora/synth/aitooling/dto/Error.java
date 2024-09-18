package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    @Schema(example = "2022-11-03T22:20:02.220029")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(example = "401")
    private int status;

    @Schema(example = "UNAUTHORIZED")
    private String error;

    @Schema(example = "Something happened :/")
    private String reason;

    @Schema(example = "/users")
    private String path;
}
