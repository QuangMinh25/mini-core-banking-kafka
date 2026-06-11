# Banking Core Service

## Implemented API

- `POST /api/v1/accounts`
- `GET /api/v1/accounts/{accountNo}`
- `POST /api/v1/transfers`

Request body:

```json
{
  "fromAccountNo": "100001",
  "toAccountNo": "100002",
  "amount": 100000,
  "currency": "VND"
}
```

Success response:

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

## Local Config

- PostgreSQL: `jdbc:postgresql://localhost:5432/banking`
- Kafka bootstrap servers: `localhost:9092`
- Kafka topic config: `app.kafka.topics.transaction-completed=transaction.completed`

## Notes

- The transfer feature builds on the existing account API and Flyway baseline in this module.
- The transfer flow publishes `TransactionCompletedEvent` after the database transaction commits.
- Kafka topic config comes from `app.kafka.topics.transaction-completed`.
