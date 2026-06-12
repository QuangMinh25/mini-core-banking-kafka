# Banking Core Service Architecture

## Overview

`banking-core-service` is an independent Gradle module in the `mini-core-banking-kafka` workspace. It is a Java 21 Spring Boot 4.1.0 application with dependencies for Web MVC, Validation, Actuator, JPA, Flyway, Kafka, and PostgreSQL.

The checked-in implementation includes:

- application entry point
- account create/get API
- transfer API
- JPA entities and repositories for accounts, transactions, and ledger entries
- Flyway schema baseline
- Kafka producer for completed transfer events
- controller and service tests

## Current Module Structure

- `src/main/java/com/minh/bankingcore/BankingCoreServiceApplication.java`
- `src/main/java/com/minh/bankingcore/account/*`
- `src/main/java/com/minh/bankingcore/common/*`
- `src/main/java/com/minh/bankingcore/transaction/*`
- `src/main/java/com/minh/bankingcore/ledger/*`
- `src/main/java/com/minh/bankingcore/kafka/*`
- `src/main/resources/application.yml`
- `src/main/resources/db/migration/V1__init_core_banking_tables.sql`
- `src/test/java/com/minh/bankingcore/BankingCoreServiceApplicationTests.java`
- `src/test/java/com/minh/bankingcore/account/*`
- `src/test/java/com/minh/bankingcore/transaction/*`
- `build.gradle`
- `settings.gradle`

## Infrastructure Signals

Workspace-level `docker-compose.yml` defines local infrastructure shared by the services:

- PostgreSQL 15
- Zookeeper
- Kafka
- Kafka UI

This infrastructure is used directly by the checked-in account and transfer features.

## Architectural Notes

- Module boundary: keep `banking-core-service` separate from `notification-service`
- Package root: `com.minh.bankingcore`
- Build and test: Gradle wrapper
- Database access model: PostgreSQL via JPA/Flyway with account, transaction, and ledger tables
- Messaging model: Kafka producer publishes `TransactionCompletedEvent` after the transfer transaction commits
- Batch model: scheduled EOD summary job aggregates prior-day transfer totals into `daily_transaction_summary`
- Security model: no Spring Security configuration found in source

## High-Risk Areas

- Financial domain logic in account and transfer flows
- SQL and migration changes on balance, transaction, and ledger tables
- Kafka event publication without an outbox pattern
- Logging or tracing that could expose sensitive banking data

## Unknown / Needs Confirmation

- Wider business domain boundaries beyond account and transfer
- Consumer-side guarantees and retry handling for downstream Kafka events
- Deployment and CI topology
