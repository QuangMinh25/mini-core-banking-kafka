# Project Profile

## Project Name

`notification-service`

## Purpose

Spring Boot service module inside the `mini-core-banking-kafka` workspace. Based on the module name and dependencies, it is intended to host notification backend logic. The checked-in source currently proves only application bootstrap, build configuration, and test scaffolding. Business capabilities are `Unknown / needs confirmation`.

## Tech Stack

- Java 21
- Gradle wrapper
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Validation
- Spring Actuator
- Spring Data JPA
- Flyway
- Spring for Apache Kafka
- PostgreSQL driver
- Lombok
- JUnit 5 via Spring Boot test support

## Build Tool

- Primary: Gradle wrapper (`gradlew`, `gradlew.bat`)
- Current safe verification target: `bash scripts/agent-check.sh`

## Main Modules And Packages

- Main package: `com.minh.notification`
- Current source package present:
  - `src/main/java/com/minh/notification`
- Current test package present:
  - `src/test/java/com/minh/notification`
- Sibling workspace module:
  - `../banking-core-service`

No controller, service, repository, entity, model, or configuration subpackages were found in checked-in source during setup.

## Entry Points

- Application bootstrap: `src/main/java/com/minh/notification/NotificationServiceApplication.java`
- Test bootstrap: `src/test/java/com/minh/notification/NotificationServiceApplicationTests.java`

## Database And Data Access

- Intended database: PostgreSQL
- Local infra is defined at workspace root in `../docker-compose.yml`
- JPA and Flyway dependencies are configured
- Checked-in entities, repositories, migrations, and SQL files: `Unknown / needs confirmation`

## Security Model

- Spring Security dependency or configuration found in source: none
- Actual authentication, authorization, and audit implementation: `Unknown / needs confirmation`
- Treat recipient and message-related data as sensitive

## External Integrations

- Kafka dependency is present
- Local Kafka, Zookeeper, and Kafka UI are defined in `../docker-compose.yml`
- Checked-in producers, consumers, Kafka topics, or contracts: `Unknown / needs confirmation`
- Email, SMS, push, or other delivery integrations: `Unknown / needs confirmation`

## Test Commands

- `bash scripts/agent-check.sh`
- `bash ./gradlew --no-daemon test`

## Docs And Ops Signals

- `HELP.md` is present but not project-specific
- Root `README.md` is empty
- Docker Compose exists at workspace root
- CI, K8s, Helm, Dockerfile, and Sonar config files were not found during setup

## Unknowns

- Real notification business flows
- API endpoints and request/response models
- Package conventions beyond the bootstrap package
- Database schema and migration baseline
- Kafka topic design and event contracts
- Delivery channel integrations and retry behavior
- Security and access-control behavior
- Deployment topology and CI pipeline details
