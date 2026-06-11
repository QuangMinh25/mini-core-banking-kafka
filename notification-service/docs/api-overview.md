# Notification Service API Overview

## Confirmed Runtime Surface

No custom HTTP controller, REST endpoint, Kafka listener, or message publisher classes were found in checked-in source during setup.

## Confirmed Application Facts

- Spring Boot application name: `notification-service`
- Spring Web MVC dependency is present
- Spring Actuator dependency is present

## Unknown / Needs Confirmation

- Custom REST endpoints
- Request and response models
- Validation rules beyond framework defaults
- Authentication and authorization requirements
- Kafka topics, payload contracts, and event schemas
- Error response format
- Versioning strategy
- Notification-delivery interfaces

## Note On Actuator

Actuator support is included as a dependency, but exposed endpoints and management configuration are `Unknown / needs confirmation` because no management properties were found beyond `spring.application.name`.
