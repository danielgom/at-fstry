package com.encora.synth.aitooling.repository;

import com.encora.synth.aitooling.model.JWTBlackList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JWTBlackListRepository extends MongoRepository<JWTBlackList, String> {

    Optional<JWTBlackList> findByExpiredToken(String expiredToken);
}
