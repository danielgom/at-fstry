package com.encora.synth.aitooling.service;

import com.encora.synth.aitooling.repository.TaskRepository;
import com.encora.synth.aitooling.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @Test
    void whenValidID_shouldDeleteTask(){
        Mockito.when(userService.getUserID()).thenReturn("1234abcd");
        taskServiceImpl.deleteById("anytask1234");
        Mockito.verify(taskRepository, Mockito.atMostOnce()).deleteByIdAndUserID("anytask1234", "1234abcd");
    }
}
