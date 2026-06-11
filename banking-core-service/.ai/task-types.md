# Task Types

Use this file only after the default context set:

1. `AGENTS.md`
2. `.ai/project-profile.md`
3. `.ai/agent-router.md`

Do not read unrelated agent or skill files.

## issue-analysis

- Files to read:
  - Relevant source, config, tests, and docs in this module
  - `.ai/agents/architect-agent.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/issue-analysis/SKILL.md`
- Agents to use: `architect-agent`, `reviewer-agent`
- Skill to use: `issue-analysis`
- Verification: trace evidence from code, Gradle files, config, and docs without modifying files
- Risks:
  - Treating inferred banking behavior as fact
  - Missing cross-service context when the issue spans `notification-service`

## bugfix

- Files to read:
  - Affected Java classes, tests, `build.gradle`, and relevant configs
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/test-agent.md`
  - `.ai/skills/bugfix/SKILL.md`
- Agents to use: `backend-agent`, `test-agent`
- Skill to use: `bugfix`
- Verification: prefer `bash scripts/agent-check.sh`; use focused Gradle test commands when they better match the scope
- Risks:
  - Fixing symptoms before the root cause is clear
  - Breaking future Kafka or database behavior hidden behind current scaffolding

## feature

- Files to read:
  - Affected source, tests, docs, `build.gradle`, and relevant sibling-service files only if integration is part of the task
  - `.ai/agents/architect-agent.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/skills/feature/SKILL.md`
- Agents to use: `architect-agent`, `backend-agent`
- Skill to use: `feature`
- Verification: scope review, targeted tests, then `bash scripts/agent-check.sh` when feasible
- Risks:
  - Inventing controller/service/repository conventions not yet present
  - Creating undocumented coupling to `notification-service`

## sql-change

- Files to read:
  - Migration files, JPA repositories, entities, SQL snippets, and PostgreSQL-related docs if they exist
  - `.ai/agents/sql-agent.md`
  - `.ai/skills/sql-change/SKILL.md`
- Agents to use: `sql-agent`
- Skill to use: `sql-change`
- Verification: syntax review, null/date review, join review, migration safety review, and focused tests when available
- Risks:
  - Inventing schema details not present in repo
  - Unsafe writes or assumptions about production-like data

## debug

- Files to read:
  - Runtime path source files, tests, `application.properties`, and relevant docs
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/test-agent.md`
  - `.ai/skills/debug/SKILL.md`
- Agents to use: `backend-agent`, `test-agent`
- Skill to use: `debug`
- Verification: reproduce if possible, prove the hypothesis, then run focused tests and `bash scripts/agent-check.sh`
- Risks:
  - Changing code before proving the failure path
  - Logging sensitive or future banking data

## tracelog-performance

- Files to read:
  - Source along the slow path, logging config if present, Kafka-related code, and DB-related code if present
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/tracelog-performance/SKILL.md`
- Agents to use: `backend-agent`, `reviewer-agent`
- Skill to use: `tracelog-performance`
- Verification: evidence-first measurement, log-safety review, focused tests, then `bash scripts/agent-check.sh`
- Risks:
  - Adding noisy logs
  - Hiding root cause by only changing timeout-related config

## sonar-fix

- Files to read:
  - Reported files, tests, and the minimum supporting context
  - `.ai/agents/reviewer-agent.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/skills/sonar-fix/SKILL.md`
- Agents to use: `reviewer-agent`, `backend-agent`
- Skill to use: `sonar-fix`
- Verification: rerun the safest relevant checks and confirm behavior did not drift
- Risks:
  - Refactoring too broadly for a small warning
  - Hiding a real defect under a cosmetic fix

## docs-update

- Files to read:
  - The docs being changed plus the code/config they describe
  - `.ai/agents/docs-agent.md`
  - `.ai/skills/docs-update/SKILL.md`
- Agents to use: `docs-agent`
- Skill to use: `docs-update`
- Verification: every claim must map to checked-in code, config, or explicit unknown status
- Risks:
  - Inventing APIs, flows, security, or schema details
  - Leaving generic framework wording in place after customization

## code-review

- Files to read:
  - Current diff or requested file set
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/code-review/SKILL.md`
- Agents to use: `reviewer-agent`
- Skill to use: `code-review`
- Verification: findings must point to concrete files and risks; note missing tests or missing evidence
- Risks:
  - Focusing on style over correctness
  - Missing cross-service, DB, or Kafka implications

## security-sensitive change

- Files to read:
  - Relevant source, tests, docs, configs, and `.ai/mcp/*.md` if external tools are used
  - `.ai/agents/security-agent.md`
  - The primary domain agent file
  - The primary task skill file
- Agents to use: `security-agent` plus domain agent
- Skill to use: primary task skill with mandatory security review
- Verification: least privilege, safe error handling, auditability, secret handling, and no sensitive-data leakage
- Risks:
  - Assuming absent security config means security is unimportant
  - Exposing customer or transaction data in logs, prompts, or MCP output
