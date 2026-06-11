# Banking Core Service API Overview

## Confirmed Runtime Surface

- `POST /api/v1/accounts`
- `GET /api/v1/accounts/{accountNo}`
- `POST /api/v1/transfers`

## Confirmed Application Facts

- Spring Boot application name: `banking-core-service`
- Spring Web MVC dependency is present
- Spring Actuator dependency is present
- Account create and get flows are implemented in-service
- Transfer flow is implemented in-service
- Success responses use `{ "success": true, "data": ... }`
- Error responses use `{ "success": false, "code": "...", "message": "..." }`

## Transfer Contract

- Request:

```json
{
  "fromAccountNo": "100001",
  "toAccountNo": "100002",
  "amount": 100000,
  "currency": "VND"
}
```

- Success response:

```json
{
  "success": true,
  "data": {
    "referenceNo": "TXN202606112230001",
    "status": "SUCCESS",
    "fromAccountNo": "100001",
    "toAccountNo": "100002",
    "amount": 100000,
    "currency": "VND"
  }
}
```

## Transfer Error Codes

- `ACCOUNT_NOT_FOUND`
- `INVALID_AMOUNT`
- `INSUFFICIENT_BALANCE`
- `ACCOUNT_INACTIVE`
- `CURRENCY_MISMATCH`
- `SAME_ACCOUNT_TRANSFER`
- `INTERNAL_ERROR`

## Kafka Publication

- Topic property: `app.kafka.topics.transaction-completed`
- Default local topic: `transaction.completed`
- Message key: `referenceNo`
- Event contract: `TransactionCompletedEvent`

## Unknown / Needs Confirmation

- Authentication and authorization requirements
- Notification-service integration

## Note On Actuator

Actuator support is included as a dependency, but exposed endpoints and management configuration are `Unknown / needs confirmation` because no management properties were found beyond `spring.application.name`.
