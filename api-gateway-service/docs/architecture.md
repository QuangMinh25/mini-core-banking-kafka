# Architecture

## Overview

`api-gateway-service` is currently a minimal Spring Boot gateway module.

- Bootstrap class: `com.minh.gateway.ApiGatewayServiceApplication`
- Config file: `src/main/resources/application.yaml`
- Test file: `src/test/java/com/minh/gateway/ApiGatewayServiceApplicationTests.java`
- Build system: Gradle wrapper with Java 21

## Current Runtime Shape

- `@SpringBootApplication` starts the service.
- `application.yaml` currently sets only `spring.application.name=api-gateway-service`.
- Spring Cloud Gateway, Actuator, and Resilience4J dependencies are declared in `build.gradle`.

## What Is Present In Code

- Application bootstrap
- Dependency declarations for gateway and resilience concerns
- A context-load integration test

## What Was Not Found

- Route definitions
- Custom gateway filters
- Controllers
- Service layer
- Repository or database code
- Spring Security configuration
- Dockerfile or Kubernetes manifests inside this module
- CI or Sonar config inside this module

## Architectural Implications

- This module should be treated as an ingress/gateway service, not a business-domain service.
- Any future request handling will likely live in gateway routes and filters rather than controller/service/repository layers.
- Auth, header propagation, route matching, circuit-breaker behavior, and actuator exposure are likely the highest-risk future extension points.

## Workspace Context

The workspace root contains `docker-compose.yml` for PostgreSQL, ZooKeeper, Kafka, and Kafka UI. No checked-in code in this module currently binds to those services, so any runtime dependency from this gateway is `Unknown / needs confirmation`.
