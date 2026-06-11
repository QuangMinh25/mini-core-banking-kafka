# Skill: Coding Discipline

## When To Use

Use for any task that changes code, docs, framework guidance, SQL, or tests in `banking-core-service`.

## Goal

Keep changes minimal, evidence-based, and aligned with the real Spring Boot module rather than the copied generic framework.

## Required Workflow

1. Read `AGENTS.md`.
2. Read `.ai/project-profile.md` and `.ai/agent-router.md`.
3. Load only the task-relevant agent file and skill file.
4. Inspect the actual source, Gradle files, tests, and docs before editing.
5. State assumptions and mark unknown facts as `Unknown / needs confirmation`.
6. Make the smallest safe change.
7. Prefer `bash scripts/agent-check.sh` for verification when feasible.
8. Report real verification outcomes and remaining risks.

## Project-Specific Reminders

- Do not read the entire `.ai/` tree by default.
- Do not invent controllers, services, repositories, entities, migrations, Kafka topics, or security flows that are not checked in.
- Do not modify business source code, refactor application code, or change runtime config during framework-maintenance tasks.
- Treat banking-domain data as sensitive even when the current repo only contains scaffolding.

## Common Mistakes To Avoid

- Copying generic framework text forward without adapting it
- Treating dependency declarations as proof of implemented behavior
- Claiming APIs or business flows that are not present in source
- Claiming tests passed without running them

## Completion Requirements

The final response must include summary, changed files, verification result, and risks / notes.
