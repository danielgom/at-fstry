package com.encora.synth.aitooling.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JWTHolder {

    private String JWTToken;

    private LocalDateTime expiresAt;
}
