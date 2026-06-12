# Notification Service API Overview

## Confirmed Runtime Surface

The service now exposes simple read/query APIs for local UI inspection:

- `GET /api/v1/notifications`
- `GET /api/v1/notifications/events/{eventId}`
- `GET /api/v1/processed-events`
- `GET /api/v1/processed-events/{eventId}`
- `GET /api/v1/notification-service/health`

## Confirmed Application Facts

- Spring Boot application name: `notification-service`
- Spring Web MVC dependency is present
- Spring Actuator dependency is present
- Notification logs are persisted in `notification_logs`
- Duplicate-event tracking is persisted in `processed_events`

## Response Shape

Application endpoints return a small wrapper:

- `success`: boolean
- `data`: payload
- `message`: error message when applicable

List endpoints return paginated `data` with:

- `content`
- `page`
- `size`
- `totalElements`
- `totalPages`

## Unknown / Needs Confirmation

- Validation rules beyond framework defaults
- Authentication and authorization requirements
- Broader Kafka topic expansion beyond `transaction.completed`
- Versioning strategy
- Notification-delivery interfaces

## Note On Actuator

Actuator support is included as a dependency, but exposed endpoints and management configuration are `Unknown / needs confirmation` because no management properties were found beyond `spring.application.name`.
