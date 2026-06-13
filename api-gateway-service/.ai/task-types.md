# Task Types

Each task type below defines the minimum extra context to load after `AGENTS.md`, `.ai/project-profile.md`, and `.ai/agent-router.md`.

## issue-analysis

- Files to read:
  - `.ai/agents/architect-agent.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/issue-analysis/SKILL.md`
  - affected source, resource, and doc files only
- Agents to use: `architect-agent`, `reviewer-agent`
- Skill to use: `issue-analysis`
- Verification: code/config evidence only unless the request explicitly asks for runnable checks
- Risks: assuming routes or security rules exist when the module currently contains only a bootstrap skeleton

## bugfix

- Files to read:
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/test-agent.md`
  - `.ai/skills/bugfix/SKILL.md`
  - affected Java and resource files only
- Agents to use: `backend-agent`, `test-agent`
- Skill to use: `bugfix`
- Verification: reproduce if possible, then run `bash scripts/agent-check.sh`
- Risks: symptom-only fixes, accidental route/filter behavior drift, unverified assumptions about downstream services

## feature

- Files to read:
  - `.ai/agents/architect-agent.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/skills/feature/SKILL.md`
  - affected Java, resource, and docs files only
- Agents to use: `architect-agent`, `backend-agent`, `test-agent`, `docs-agent`
- Skill to use: `feature`
- Verification: acceptance check, focused tests, `bash scripts/agent-check.sh`, doc update if behavior changes
- Risks: inventing gateway topology, route contracts, or fallback semantics not backed by requirements

## sql-change

- Files to read:
  - `.ai/agents/sql-agent.md`
  - `.ai/skills/sql-change/SKILL.md`
  - any actual persistence files if present
- Agents to use: `sql-agent`, `backend-agent`
- Skill to use: `sql-change`
- Verification: confirm the change belongs in this service, then validate syntax and read-only investigation only
- Risks: this module currently has no SQL or DB code; the real fix may belong in another service

## debug

- Files to read:
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/test-agent.md`
  - `.ai/skills/debug/SKILL.md`
  - `build.gradle`
  - `src/main/resources/application.yaml`
  - affected source files only
- Agents to use: `backend-agent`, `test-agent`, `reviewer-agent`
- Skill to use: `debug`
- Verification: reproduce, trace startup and request path, validate hypothesis, run `bash scripts/agent-check.sh`
- Risks: guessing route/filter flow, changing config to hide the issue, exposing sensitive headers in debug output

## tracelog-performance

- Files to read:
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/tracelog-performance/SKILL.md`
  - affected logging, route, or filter files only
- Agents to use: `backend-agent`, `reviewer-agent`, `security-agent`
- Skill to use: `tracelog-performance`
- Verification: confirm log usefulness, confirm no secrets logged, run `bash scripts/agent-check.sh` if code changes
- Risks: noisy logs, leaked auth headers, measuring the wrong boundary, "performance" edits without evidence

## sonar-fix

- Files to read:
  - `.ai/agents/reviewer-agent.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/skills/sonar-fix/SKILL.md`
  - only the flagged files
- Agents to use: `reviewer-agent`, `backend-agent`, `test-agent`
- Skill to use: `sonar-fix`
- Verification: lowest-risk fix plus `bash scripts/agent-check.sh`
- Risks: broad cleanup refactors, behavior drift, silent suppression of real warnings

## docs-update

- Files to read:
  - `.ai/agents/docs-agent.md`
  - `.ai/skills/docs-update/SKILL.md`
  - relevant Java, YAML, Gradle, and docs files only
- Agents to use: `docs-agent`, `architect-agent`
- Skill to use: `docs-update`
- Verification: every statement should trace back to checked-in code or config
- Risks: inventing APIs, assuming route tables, copying generic Spring text that is not project-specific

## code-review

- Files to read:
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/code-review/SKILL.md`
  - diff and directly affected files
- Agents to use: `reviewer-agent`, `security-agent`, `test-agent`
- Skill to use: `code-review`
- Verification: findings-first review, severity classification, explicit test gaps
- Risks: style-only feedback, missing gateway-specific security concerns, overlooking config and route regressions

## security-sensitive change

- Files to read:
  - `.ai/agents/security-agent.md`
  - primary domain agent file
  - primary task skill
  - any auth, header, filter, actuator, or downstream credential files
- Agents to use: `security-agent`, domain agent, `reviewer-agent`
- Skill to use: primary domain skill plus security review
- Verification: least privilege, secret handling, header propagation review, safe failure behavior, `bash scripts/agent-check.sh`
- Risks: auth bypass, token leakage, open actuator exposure, unsafe retry or fallback behavior
