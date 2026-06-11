# SQL Change Skill

## When To Use

Use for PostgreSQL, Flyway, JPA-query, or schema-related changes in `banking-core-service`.

## Goal

Produce a correct, safe, and performance-aware SQL change without inventing missing schema details.

## Required Workflow

1. Identify the exact SQL or schema unit affected.
2. Verify that the repository actually contains the table, migration, or query being changed.
3. Validate syntax, joins, filters, null handling, date handling, and expected row shape.
4. Review migration safety and performance risk.
5. Run the safest relevant verification and document residual risks.

## Common Mistakes To Avoid

- Assuming schema details from the service name alone
- Ignoring full-scan or cardinality risk
- Using write-capable DB tooling for analysis

## Completion Requirements

Include SQL areas changed, correctness checks, performance notes, verification result, and remaining risks.
