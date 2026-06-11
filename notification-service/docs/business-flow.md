# Notification Service Business Flow

## Confirmed Current Flow

The checked-in code confirms only the application bootstrap flow:

1. Spring Boot starts `NotificationServiceApplication`
2. The application loads configuration from `application.properties`
3. The test suite currently verifies that the Spring context can start

## Intended Domain Direction

Based on the module name, this service is likely intended to host notification behavior. The actual implemented business flows are `Unknown / needs confirmation` because no controllers, services, repositories, entities, migrations, or Kafka handlers are checked in yet.

## Messaging And Integration Flow

- Kafka dependencies are present
- Local Kafka infrastructure exists in the workspace root `docker-compose.yml`
- Checked-in producers, consumers, topics, retry flows, and dead-letter handling: `Unknown / needs confirmation`

## Data Flow

- PostgreSQL, JPA, and Flyway dependencies are present
- Checked-in schema, migrations, entities, and repository flows: `Unknown / needs confirmation`

## Cross-Service Flow

`banking-core-service` exists as a sibling module in the same workspace. A real business flow between the services is `Unknown / needs confirmation` because no integration code is checked in yet.
