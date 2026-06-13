# Project Profile

## Project Name

`api-gateway-service`

## Purpose

Spring Boot gateway service for the `mini-core-banking-kafka` workspace. Based on the checked-in code, the module is currently a gateway scaffold rather than a completed API gateway implementation.

## Tech Stack

- Java 21
- Gradle
- Spring Boot 4.1.0
- Spring Cloud Gateway
- Spring Cloud Circuit Breaker with Resilience4J
- Spring Boot Actuator
- JUnit 5 / Spring Boot Test

## Build Tool

- Primary build tool: Gradle wrapper (`gradlew`, `gradlew.bat`)
- Main build file: `build.gradle`
- Settings file: `settings.gradle`

## Main Modules / Packages

- `src/main/java/com/minh/gateway`
  - `ApiGatewayServiceApplication.java`
- `src/main/resources`
  - `application.yaml`
- `src/test/java/com/minh/gateway`
  - `ApiGatewayServiceApplicationTests.java`

No additional packages, route config classes, controllers, filters, services, repositories, or domain models were found.

## Entry Points

- Application bootstrap: `src/main/java/com/minh/gateway/ApiGatewayServiceApplication.java`
- Runtime configuration root: `src/main/resources/application.yaml`
- Verification harness: `scripts/agent-check.sh`

## Source Structure Notes

- Current structure is a minimal Spring Boot application skeleton.
- No controller/service/repository/entity/model/config layering is implemented yet beyond the root application class.
- No dedicated route or filter package exists yet.

## Database / Data Access

- No database driver, JPA starter, JDBC starter, repository package, SQL file, or migration tool was found in this module.
- Root workspace `docker-compose.yml` starts PostgreSQL, but this service does not currently reference PostgreSQL in checked-in code.

## Security Model

- No Spring Security dependency or `SecurityFilterChain` class was found in this module.
- No auth filter, token relay, CORS policy, or gateway authorization rules were found.
- Security model: `Unknown / needs confirmation`

## External Integrations

- Declared framework integrations:
  - Spring Cloud Gateway
  - Resilience4J circuit breaker
  - Spring Boot Actuator
- Actual downstream routes, service URLs, Kafka usage, or third-party integrations in this module: `Unknown / needs confirmation`

## Test Commands

- Safe default verification: `bash scripts/agent-check.sh`
- Windows fallback: `gradlew.bat --no-daemon test`
- Direct Gradle test command in bash-compatible shells: `./gradlew --no-daemon test`

## Docs / Repository Notes

- `HELP.md` is the default Spring-generated help file and is not project-specific.
- No project-specific README content was found in this module or the workspace root.
- No Dockerfile, Kubernetes manifests, Helm charts, CI pipelines, or Sonar config files were found inside this module.

## Unknowns

- Gateway route table
- Request/response transformation rules
- Security and auth design
- Downstream service contracts
- Actuator exposure policy
- Deployment topology for this module
