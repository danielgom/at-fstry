package com.encora.synth.aitooling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {

    @Schema(example = "jwttoken")
    private String token;

    @Schema(example = "aaaabbbcccccc")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_at")
    @Schema(example = "2022-11-03T23:49:59.524096")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    @JsonProperty("user")
    private UserGetResponse userGetResponse;
}
