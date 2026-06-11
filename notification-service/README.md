# Notification Service

`notification-service` consumes `transaction.completed` events and persists:

- one row in `notification_logs`
- one row in `processed_events`

The consumer is idempotent by `processed_events.event_id`. Re-consuming the same `eventId` does not create a duplicate notification log and does not crash the service.

Flyway stores this service's migration state in `flyway_schema_history_notification_service` and baselines at version `1` so it can share the local `banking` database without colliding with other modules' migration history before applying this service's `V2` migration.

## Local Kafka Recovery

Malformed records are skipped by the consumer after a single warning. If local offsets still need to be moved past old test data, stop `notification-service` and run:

```bash
docker exec -it banking-kafka kafka-consumer-groups --bootstrap-server banking-kafka:29092 --group notification-service --topic transaction.completed --reset-offsets --to-latest --execute
```

To recreate the local topic instead:

```bash
docker exec -it banking-kafka kafka-topics --bootstrap-server banking-kafka:29092 --delete --topic transaction.completed
docker exec -it banking-kafka kafka-topics --bootstrap-server banking-kafka:29092 --create --topic transaction.completed --partitions 1 --replication-factor 1
```
