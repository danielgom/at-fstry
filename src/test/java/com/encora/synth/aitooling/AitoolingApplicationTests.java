package com.encora.synth.aitooling;

import com.encora.synth.aitooling.controller.TaskController;
import com.encora.synth.aitooling.controller.UserController;
import com.encora.synth.aitooling.service.TaskService;
import com.encora.synth.aitooling.service.UserService;
import com.encora.synth.aitooling.utils.MongoDBContainerTestExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext
@ExtendWith(MongoDBContainerTestExtension.class)
class AitoolingApplicationTests {

	@Autowired
	private UserController userController;

	@Autowired
	private TaskController taskController;

	@Autowired
	private UserService userService;

	@Autowired
	private TaskService taskService;

	@Test
	void contextLoads() {
		Assertions.assertThat(userController).isNotNull();
		Assertions.assertThat(taskController).isNotNull();
		Assertions.assertThat(userService).isNotNull();
		Assertions.assertThat(taskService).isNotNull();
	}

}
