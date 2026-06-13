# Agent Router

Use this file after reading `AGENTS.md` and `.ai/project-profile.md`.

Default first reads for almost every task:

1. `AGENTS.md`
2. `.ai/project-profile.md`
3. `.ai/agent-router.md`

Then load only the task-specific files below.

## issue-analysis

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/architect-agent.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/issue-analysis/SKILL.md`
  - `build.gradle`
  - `src/main/resources/application.yaml`
- Lead agents: `architect-agent`, `reviewer-agent`
- Supporting agents: `security-agent` for auth/header propagation concerns, `test-agent` for reproduction gaps
- Verification: evidence-based code reading only unless the task explicitly needs runtime checks

## bugfix

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/test-agent.md`
  - `.ai/skills/bugfix/SKILL.md`
  - affected Java, resource, or doc files only
- Lead agents: `backend-agent`, `test-agent`
- Supporting agents: `security-agent` for gateway auth or header forwarding, `architect-agent` when scope is unclear
- Verification: focused reproduction first, then `bash scripts/agent-check.sh` when feasible

## feature

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/architect-agent.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/skills/feature/SKILL.md`
  - affected source and config files only
- Lead agents: `architect-agent`, `backend-agent`
- Supporting agents: `security-agent`, `test-agent`, `docs-agent`
- Verification: route/filter/security impact review plus Gradle verification

## sql-change

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/sql-agent.md`
  - `.ai/skills/sql-change/SKILL.md`
  - any actual SQL or persistence files if they exist
- Lead agent: `sql-agent`
- Supporting agents: `backend-agent`, `test-agent`
- Verification: first confirm the SQL change belongs in this module; current repository evidence suggests it usually does not

## debug

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/test-agent.md`
  - `.ai/skills/debug/SKILL.md`
  - `build.gradle`
  - `src/main/resources/application.yaml`
  - affected source files only
- Lead agents: `backend-agent`, `test-agent`
- Supporting agents: `architect-agent`, `security-agent`, `reviewer-agent`
- Verification: reproduce, trace startup/route/filter/downstream boundaries, then run safe checks

## tracelog-performance

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/tracelog-performance/SKILL.md`
  - affected startup, routing, or logging files only
- Lead agents: `backend-agent`, `reviewer-agent`
- Supporting agents: `security-agent`, `test-agent`
- Verification: confirm logs stay minimal and safe; use Gradle test script when code changes occur

## sonar-fix

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/agents/backend-agent.md`
  - `.ai/skills/sonar-fix/SKILL.md`
  - only the flagged files
- Lead agents: `reviewer-agent`, `backend-agent`
- Supporting agents: `test-agent`
- Verification: lowest-risk remediation plus safe Gradle verification

## docs-update

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/docs-agent.md`
  - `.ai/skills/docs-update/SKILL.md`
  - relevant source/config/build files
  - only the impacted docs
- Lead agent: `docs-agent`
- Supporting agents: `architect-agent`, `backend-agent`
- Verification: every doc statement must trace back to checked-in code or config

## code-review

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/reviewer-agent.md`
  - `.ai/skills/code-review/SKILL.md`
  - diff plus directly affected files
- Lead agent: `reviewer-agent`
- Supporting agents: `security-agent`, `test-agent`, `architect-agent`
- Verification: findings-first review with explicit risk and evidence

## security-sensitive change

- Read:
  - `.ai/task-types.md`
  - `.ai/agents/security-agent.md`
  - primary domain agent file
  - primary task skill
  - any auth, header, filter, or actuator-related files
- Lead agents: `security-agent` plus domain agent
- Supporting agents: `architect-agent`, `test-agent`, `reviewer-agent`
- Verification: least-privilege review, secret-handling review, safe failure-path review
