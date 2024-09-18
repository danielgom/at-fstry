package com.encora.synth.aitooling.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class JWTProvider {

    @Value("${JWT.secret}")
    private String JWTSecretKEY;

    @Value("${JWT.expire-duration-hours}")
    private int JWTExpireDurationHours;

    public JWTHolder generateToken(String userID, String email) {
        Instant currentTime = Instant.now();

        return JWTHolder.builder().JWTToken(
                        JWT.create()
                                .withIssuedAt(currentTime)
                                .withExpiresAt(currentTime.plus(Duration.ofHours(JWTExpireDurationHours)))
                                .withSubject(email)
                                .withClaim("id", userID)
                                .withIssuer("com.encore.synth.aitooling")
                                .sign(Algorithm.HMAC256(JWTSecretKEY)))
                .expiresAt(LocalDateTime.ofInstant(currentTime.plus(Duration.ofHours(JWTExpireDurationHours)),
                        ZoneId.of("America/Mexico_City")))
                .build();
    }

    public boolean isValidToken(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWTSecretKEY))
                    .withIssuer("com.encore.synth.aitooling")
                    .build();
            verifier.verify(jwtToken);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public String getSubjectFromJWT(String jwtToken) {
        return JWT.decode(jwtToken).getSubject();
    }
}
