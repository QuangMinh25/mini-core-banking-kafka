# Debug Skill

## When To Use

Use for runtime exceptions, wrong results, startup failures, Kafka-flow issues, PostgreSQL/JPA issues, validation bugs, or environment-specific defects in `banking-core-service`.

## Goal

Find the root cause safely and avoid random code changes.

## Required Workflow

1. Read `AGENTS.md`, `.ai/project-profile.md`, and the routed agent files.
2. Understand expected behavior from code or approved requirements.
3. Reproduce the issue or define the missing reproduction data.
4. Trace the flow from entry point to persistence, Kafka, or output boundary.
5. Form a hypothesis and verify it with code reading, logs, tests, or safe local checks.
6. Apply a minimal fix only after the root cause is clear.
7. Run focused verification and `bash scripts/agent-check.sh`.
8. Report root cause, fix, changed files, verification, and risks.

## Debug Checklist

- Startup path checked
- Validation and exception handling checked
- `application.properties` impact checked
- PostgreSQL/JPA/Flyway path checked if relevant
- Kafka path checked if relevant
- Sensitive-data logging risk checked

## Common Mistakes To Avoid

- Guessing without tracing the path
- Changing code before proving the hypothesis
- Logging sensitive data while debugging

## Completion Requirements

Include root cause summary, fix summary, changed files, verification result, and remaining risks.
