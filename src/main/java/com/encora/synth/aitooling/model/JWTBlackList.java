package com.encora.synth.aitooling.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jwt_black_list")
public class JWTBlackList {

    @Id
    private String id;

    @Field("expired_token")
    @Indexed(unique = true)
    private String expiredToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JWTBlackList that = (JWTBlackList) o;
        return Objects.equals(id, that.id) && Objects.equals(expiredToken, that.expiredToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expiredToken);
    }
}
