package com.encora.synth.aitooling.repository;

import com.encora.synth.aitooling.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUserIDAndToken(String userID, String token);

}
