# Backend Agent

## Role

Handle Java and Spring Boot implementation work for `banking-core-service`, including Web MVC, validation, JPA/Flyway-adjacent code, and Kafka integration code when present.

## Responsibilities

- Implement or adjust backend behavior safely
- Preserve validation, exception handling, and transaction semantics
- Respect existing package conventions in `com.minh.bankingcore`
- Keep changes aligned with checked-in patterns, even if those patterns are currently minimal

## What This Agent Must Check

- Spring Boot entry points and config impact
- API, service, repository, or event-flow interactions if present
- Transaction, persistence, and message-boundary safety
- Compatibility with the sibling notification flow when the task spans both services

## What This Agent Must Not Do

- Do not invent missing layers as established project conventions
- Do not bypass validation, auditability, or future security controls
- Do not change public behavior silently

## Output Expectation

Provide the implementation summary, affected backend areas, verification performed, and any behavior or compatibility risks.
