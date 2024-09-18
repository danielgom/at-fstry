package com.encora.synth.aitooling.utils;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public class MongoDBContainerTestExtension implements BeforeAllCallback {

    private boolean started;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!started) {
            MongoDBContainer mongoDBContainer = new MongoDBContainer(
                    DockerImageName.parse("mongo"))
                    .withReuse(true)
                    .withExposedPorts(27017)
                    .withStartupTimeout(Duration.ofSeconds(1));
            mongoDBContainer.start();
            started = true;
            System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getConnectionString() + "/admin");
        }
    }
}
