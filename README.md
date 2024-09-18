# AI Tooling Application

This is a Spring Boot 3 application for AI Tooling that connects to a MongoDB database. This guide provides instructions to set up and run the application locally.

## Prerequisites

Before running the application, ensure you have the following installed:

- **Docker**: To run the MongoDB container.
- **Java 21**: Required for running Spring Boot 3.
- **Gradle**: You can use the wrapper provided with the project (`./gradlew`).

## Getting Started

### 1. Clone the Repository

Clone the repository to your local machine:

```sh
git clone <your-repo-url>
cd <your-repo-directory>

MONGO_DB_PORT=27017             # Port for MongoDB
MONGO_DB_USERNAME=your_user     # MongoDB Username
MONGO_DB_PASSWORD=your_password # MongoDB Password
MONGO_DB_NAME=your_db_name      # MongoDB Database Name
MONGO_DB_HOST=localhost         # Host for MongoDB (use 'localhost' if running locally)

docker-compose up -d

./gradlew bootRun
```

#### This is the current configuration file we have for our spring boot application

```yaml
spring:
  application:
    name: aitooling
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
  data:
    mongodb:
      uri: mongodb://${MONGO_DB_USERNAME}:${MONGO_DB_PASSWORD}@${MONGO_DB_HOST}:${MONGO_DB_PORT}/${MONGO_DB_NAME}
      auto-index-creation: true
JWT:
  secret: "secret"
  expire-duration-hours: 1

RefreshToken:
  expire-duration-days: 7

```

### 2. Start the application

By default, we can use the following command to start the application,
note that this will use the default environment variables that can be found in the .env file
please feel free to change it for local development purposes:

```
docker-compose up
```

.env file

```
MONGO_DB_PORT=27017             # Port for MongoDB
MONGO_DB_USERNAME=your_user     # MongoDB Username
MONGO_DB_PASSWORD=your_password # MongoDB Password
MONGO_DB_NAME=your_db_name      # MongoDB Database Name
MONGO_DB_HOST=localhost         # Host for MongoDB (use 'localhost' if running locally)
```

We can either choose to update these variables with a single command

```
MONGO_DB_PORT=27017 MONGO_DB_USERNAME=your_user MONGO_DB_PASSWORD=your_password MONGO_DB_NAME=your_db_name docker-compose up -d

```

### Stop the application
```
docker-compose down
```

### Run Backend Tests
```
./gradlew test
```

### Explanation of Sections

- **Prerequisites**: Details the software required to run the application.
- **Getting Started**: Step-by-step instructions to set up the environment, run the MongoDB container, and start the Spring Boot application.
- **Configuration**: Shows the application's configuration, especially for MongoDB connection.
- **Stopping the Services**: Instructions on how to stop the MongoDB service and the Spring Boot application.
- **Running Tests**: Details on running tests for the application.
- **Troubleshooting**: Provides solutions for common issues.
- **License and Contributing**: General information about licensing and contribution guidelines.

You can replace `<your-repo-url>` and `<your-repo-directory>` with your actual repository URL and directory.
