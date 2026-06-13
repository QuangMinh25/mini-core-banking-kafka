# Mini Core Banking Kafka

## Swagger / OpenAPI

Local service URLs:

`banking-core-service`
`http://localhost:8081/swagger-ui.html`
`http://localhost:8081/v3/api-docs`

`notification-service`
`http://localhost:8082/swagger-ui.html`
`http://localhost:8082/v3/api-docs`

## API Gateway Swagger

Central Swagger UI:
`http://localhost:8080/swagger-ui.html`

OpenAPI JSON:
`http://localhost:8080/v3/api-docs`

Core API docs via gateway:
`http://localhost:8080/v3/api-docs/core`

Notification API docs via gateway:
`http://localhost:8080/v3/api-docs/notification`

Acceptance criteria:

- Developers can open only the gateway Swagger UI and inspect all service APIs.
- Swagger UI has separate groups for Gateway, Core Service, and Notification Service.
- Gateway-relative OpenAPI URLs work behind localhost.
- Existing API routes still work.
- If one downstream service is down, gateway should return a clear error when its docs endpoint is requested.
