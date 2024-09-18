# First stage: Build the application
FROM gradle:jdk21-jammy AS build

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the application
RUN ./gradlew bootJar

# Second stage: Run the application
FROM eclipse-temurin:21.0.4_7-jre-jammy

WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/aitooling-v1.0.0.jar aitooling-v1.0.0-BE.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "aitooling-v1.0.0-BE.jar"]
