package com.encora.synth.aitooling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_token")
public class RefreshToken {

    @Id
    private String id;

    @Field(value = "user_id")
    private String userID;

    @Field(value = "token")
    @Indexed(unique = true)
    private String token;

    @Field(value = "expires_at")
    private LocalDateTime expiresAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id) && Objects.equals(userID, that.userID) &&
                Objects.equals(token, that.token) && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userID, token, expiresAt);
    }
}
