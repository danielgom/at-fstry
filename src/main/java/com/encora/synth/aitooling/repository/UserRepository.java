package com.encora.synth.aitooling.repository;

import com.encora.synth.aitooling.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String s);

}
