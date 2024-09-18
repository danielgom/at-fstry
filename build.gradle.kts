plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.encora.synth"
version = "v1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
	implementation("org.springdoc:springdoc-openapi-security:1.8.0")
	implementation("org.mapstruct:mapstruct:1.6.0")
	implementation("commons-validator:commons-validator:1.9.0") {
		exclude("commons-logging", "commons-logging")
	}
	implementation("com.auth0:java-jwt:4.4.0")

	compileOnly("org.projectlombok:lombok")

	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:testcontainers:1.20.1")
	testImplementation("org.testcontainers:junit-jupiter:1.20.1")
	testImplementation("org.testcontainers:mongodb:1.20.1")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}
