# API Gateway Service

`api-gateway-service` is the entry point for the Mini Core Banking APIs.

## Circuit Breaker

The gateway uses Resilience4j circuit breakers to stop repeated downstream failures from cascading into the client experience.

Configured circuit breakers:

- `banking-core-service-cb`
- `notification-service-cb`

Protected route groups:

- `/api/v1/accounts/**`
- `/api/v1/transfers/**`
- `/api/v1/transactions/**`
- `/api/v1/outbox-events/**`
- `/api/v1/audit-logs/**`
- `/api/v1/notifications/**`
- `/api/v1/processed-events/**`

## Why The Gateway Uses It

- Keeps client-facing failures consistent when a downstream service is unavailable.
- Avoids repeated connection attempts when a service is already unhealthy.
- Returns a clean JSON fallback payload with the correlation id preserved.

## How To Test

1. Start the gateway.
2. Start `banking-core-service`.
3. Stop `notification-service`.
4. Call a notification route through the gateway, for example `GET /api/v1/notifications`.
5. Verify the fallback payload returns `503` with `code=SERVICE_UNAVAILABLE`.
6. Trigger enough failures to open the circuit breaker and confirm the gateway keeps returning the fallback until the downstream service recovers.
7. Restart the downstream service and confirm requests succeed again once the breaker transitions back to `CLOSED`.

Actuator endpoints:

- `GET /actuator/health`
- `GET /actuator/circuitbreakers`
