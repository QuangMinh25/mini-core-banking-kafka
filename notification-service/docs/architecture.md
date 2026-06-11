# Notification Service Architecture

## Overview

`notification-service` is an independent Gradle module in the `mini-core-banking-kafka` workspace. It is a Java 21 Spring Boot 4.1.0 application with dependencies for Web MVC, Validation, Actuator, JPA, Flyway, Kafka, and PostgreSQL.

The checked-in implementation is currently bootstrap-stage:

- application entry point exists
- context-load test exists
- application name is configured
- no controllers, services, repositories, entities, migrations, or Kafka handlers were found in source during setup

## Current Module Structure

- `src/main/java/com/minh/notification/NotificationServiceApplication.java`
- `src/main/resources/application.properties`
- `src/test/java/com/minh/notification/NotificationServiceApplicationTests.java`
- `build.gradle`
- `settings.gradle`

## Infrastructure Signals

Workspace-level `docker-compose.yml` defines local infrastructure shared by the services:

- PostgreSQL 15
- Zookeeper
- Kafka
- Kafka UI

This confirms local development intent around PostgreSQL and Kafka, but it does not prove current in-code producer, consumer, schema, or delivery implementations.

## Architectural Notes

- Module boundary: keep `notification-service` separate from `banking-core-service`
- Package root: `com.minh.notification`
- Build and test: Gradle wrapper
- Database access model: intended to be PostgreSQL via JPA/Flyway, but concrete repositories and schema are `Unknown / needs confirmation`
- Messaging model: Kafka dependency is present, but concrete event contracts are `Unknown / needs confirmation`
- Security model: no Spring Security configuration found in source

## High-Risk Areas

- Future recipient, template, or delivery logic in this service
- SQL or migration changes once schema files exist
- Kafka event publication or consumption once handlers exist
- Logging or tracing that could expose sensitive notification data

## Unknown / Needs Confirmation

- Actual business domain boundaries inside this service
- REST endpoints and request/response contracts
- Entity and repository package structure
- Flyway migration baseline
- Kafka topics, keys, and payload contracts
- Notification channel integrations
- Deployment and CI topology
