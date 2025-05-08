# Tool configurations
DC := docker compose
MVN := mvn
TAIL := tail -f

# Project configurations
SPRING_DIR := graphqlserver
LOG_FILE := logs/spring.log
KAFKA_SERVICE := kafka

# Docker options
DC_DOWN_OPTS := --volumes --remove-orphans

# Default target
.DEFAULT_GOAL := help

.PHONY: help up down restart logs boot boot-logs

help:		## Show this help message
	@echo 'Usage:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# Docker services management
up:		## Start all containers in detached mode
	$(DC) up -d

down:		## Stop and remove all containers and volumes
	$(DC) down $(DC_DOWN_OPTS)

restart: down up	## Restart all containers

# Monitoring
logs:		## Follow Kafka container logs
	$(DC) logs -f $(KAFKA_SERVICE)

boot-logs:	## Follow Spring Boot application logs
	cd $(SPRING_DIR) && $(TAIL) $(LOG_FILE)

# Application management
boot:		## Build and run Spring Boot application
	cd $(SPRING_DIR) && $(MVN) clean install -DskipTests && $(MVN) spring-boot:run