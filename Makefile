# Define variables for directories
FRONTEND_DIR=fe/frontend

# Default environment variables file
ENV_FILE=.env

# Phony targets to ensure commands are always run
.PHONY: all build run stop docker-setup

# Default target: Run both backend and frontend
all: docker-setup run

# Target to build and run both backend and frontend
run: run-backend run-frontend

# Target to run backend (API)
run-backend:
	@echo "Starting the backend API..."
	./gradlew bootRun &

# Target to run frontend
run-frontend:
	@echo "Starting the frontend..."
	cd $(FRONTEND_DIR) && ng serve --open &

# Target to stop backend and frontend
stop:
	@echo "Stopping all services..."
	@pkill -f "java -jar" || true
	@pkill -f "ng serve" || true
	@echo "All services stopped."

# Target to set up Docker and MongoDB
docker-setup:
	@echo "Loading environment variables..."
	@export $(shell grep -v '^#' $(ENV_FILE) | xargs)
	@echo "Starting Application..."
	docker-compose up -d
	@echo "Application started successfully."
	@echo "If run locally please go to localhost:4200"

# Target to run tests
test:
	@echo "Running tests..."
	./gradlew test
