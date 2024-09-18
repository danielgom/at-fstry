package com.encora.synth.aitooling.repository;

import com.encora.synth.aitooling.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findAllByUserID(String userID);

    Optional<Task> findByIdAndUserID(String id, String userID);

    void deleteByIdAndUserID(String id, String userID);

}
