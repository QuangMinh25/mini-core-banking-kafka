# Bugfix Skill

## When To Use

Use for correcting an existing defect in `notification-service` with the smallest safe behavior change.

## Goal

Find the root cause, apply a minimal fix, and reduce regression risk across Spring Boot, PostgreSQL, and Kafka-adjacent behavior.

## Required Workflow

1. Read `AGENTS.md`, `.ai/project-profile.md`, and the routed agent files.
2. Confirm the defect scope and whether it is local to this service or cross-service.
3. Inspect the affected Java, config, SQL, or doc path before editing.
4. Apply the smallest safe correction.
5. Run focused verification and `bash scripts/agent-check.sh` when appropriate.
6. Report root cause, fix summary, verification result, and residual risks.

## Common Mistakes To Avoid

- Fixing symptoms without proving the cause
- Assuming missing notification logic exists somewhere unseen
- Broad refactors during a narrow fix

## Completion Requirements

Include root cause, minimal fix summary, changed files, actual verification, and residual risk notes.
