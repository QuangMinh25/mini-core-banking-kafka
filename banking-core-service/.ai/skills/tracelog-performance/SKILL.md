# Trace Log And Performance Skill

## When To Use

Use for tracing request or event flow, improving observability, or investigating slow behavior in `banking-core-service`.

## Goal

Add only useful evidence, preserve behavior, and avoid leaking sensitive banking-domain data.

## Required Workflow

1. Read `AGENTS.md`, `.ai/project-profile.md`, and the routed agent files.
2. Identify whether the path is HTTP, startup, DB, Kafka, or cross-service.
3. Map entry, key boundaries, and exit points.
4. Check existing logging and timing conventions before adding anything.
5. Measure or inspect evidence before optimizing.
6. Add minimal logs only at important boundaries if the task requires code changes.
7. Run focused verification and `bash scripts/agent-check.sh`.

## Safe Logging Rules

- Never log secrets, credentials, tokens, connection strings, or full sensitive business payloads
- Prefer IDs and high-level state over payload dumps
- Keep log volume low

## Performance Focus Areas

- Startup behavior
- JPA and PostgreSQL access
- Kafka producer or consumer latency
- Unnecessary loops or blocking calls
- Timeout and retry behavior

## Completion Requirements

Include the traced flow, evidence found, changes made if any, logs added if any, verification result, and next steps.
