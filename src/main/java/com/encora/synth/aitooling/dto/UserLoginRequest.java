package com.encora.synth.aitooling.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginRequest {

    @Schema(example = "my_email@outlook.com")
    private String email;

    @Schema(example = "superPass")
    private String password;
}
