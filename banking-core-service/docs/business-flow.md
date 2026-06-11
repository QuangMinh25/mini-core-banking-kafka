# Banking Core Service Business Flow

## Confirmed Account Flow

1. Client creates an account with `POST /api/v1/accounts`
2. The service validates the request and creates an `ACTIVE` account
3. Client can retrieve account details with `GET /api/v1/accounts/{accountNo}`

## Confirmed Transfer Flow

1. Client calls `POST /api/v1/transfers`
2. The service validates amount, account existence, different source and destination, active status, matching currency, and sufficient balance
3. The service loads both accounts under a write lock inside one `@Transactional` boundary
4. The source account is debited and the destination account is credited
5. One `transactions` row is saved with `type = TRANSFER` and `status = SUCCESS`
6. Two `ledger_entries` rows are saved:
   - source account as `DEBIT`
   - destination account as `CREDIT`
7. After commit, `TransactionCompletedEvent` is published to Kafka with `referenceNo` as the key

## Messaging And Integration Flow

- Kafka producer is implemented in-service
- Topic name comes from `app.kafka.topics.transaction-completed`
- Notification-service consumption is not implemented here

## Data Flow

- Flyway baseline creates `accounts`, `transactions`, and `ledger_entries`
- JPA repositories are used for account, transaction, and ledger persistence
- Money values use `BigDecimal`

## Unknown / Needs Confirmation

- Downstream retry, dead-letter, and outbox strategy
- Authentication and authorization requirements
