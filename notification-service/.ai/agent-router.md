# Agent Router

Use this file after reading `AGENTS.md` and `.ai/project-profile.md`.

Default context rule:

1. Do not read the whole `.ai/` tree.
2. Read `AGENTS.md`, `.ai/project-profile.md`, and this file first.
3. Read `.ai/task-types.md` only for the active task type.
4. Load only the matching agent file and matching skill file.
5. Read `.ai/mcp/*.md` only if MCP or external tools are needed.

## Task Routing

### issue-analysis

- Read: `.ai/task-types.md`, `.ai/agents/architect-agent.md`, `.ai/agents/reviewer-agent.md`, `.ai/skills/issue-analysis/SKILL.md`
- Lead agents: `architect-agent`, `reviewer-agent`
- Supporting agents: `sql-agent`, `security-agent`, `test-agent`, `docs-agent`
- Use when: investigating this service without modifying code

### bugfix

- Read: `.ai/task-types.md`, `.ai/agents/backend-agent.md`, `.ai/agents/test-agent.md`, `.ai/skills/bugfix/SKILL.md`
- Lead agents: `backend-agent`, `test-agent`
- Supporting agents: `sql-agent`, `security-agent`, `reviewer-agent`
- Use when: fixing a defect in Java, Spring, Kafka, JPA, Flyway, or docs-backed behavior

### feature

- Read: `.ai/task-types.md`, `.ai/agents/architect-agent.md`, `.ai/agents/backend-agent.md`, `.ai/skills/feature/SKILL.md`
- Lead agents: `architect-agent`, `backend-agent`
- Supporting agents: `sql-agent`, `security-agent`, `test-agent`, `docs-agent`
- Use when: adding behavior inside this service after scope and boundaries are clear

### sql-change

- Read: `.ai/task-types.md`, `.ai/agents/sql-agent.md`, `.ai/skills/sql-change/SKILL.md`
- Lead agent: `sql-agent`
- Supporting agents: `backend-agent`, `test-agent`, `security-agent`
- Use when: changing JPA queries, migration SQL, PostgreSQL structure, or schema assumptions

### debug

- Read: `.ai/task-types.md`, `.ai/agents/backend-agent.md`, `.ai/agents/test-agent.md`, `.ai/skills/debug/SKILL.md`
- Lead agents: `backend-agent`, `test-agent`
- Supporting agents: `architect-agent`, `sql-agent`, `security-agent`, `reviewer-agent`
- Use when: tracing runtime failures, Kafka issues, config issues, or wrong results

### tracelog-performance

- Read: `.ai/task-types.md`, `.ai/agents/backend-agent.md`, `.ai/agents/reviewer-agent.md`, `.ai/skills/tracelog-performance/SKILL.md`
- Lead agents: `backend-agent`, `reviewer-agent`
- Supporting agents: `sql-agent`, `security-agent`, `test-agent`
- Use when: improving observability or investigating timeouts, slow SQL, or Kafka flow timing

### sonar-fix

- Read: `.ai/task-types.md`, `.ai/agents/reviewer-agent.md`, `.ai/agents/backend-agent.md`, `.ai/skills/sonar-fix/SKILL.md`
- Lead agents: `reviewer-agent`, `backend-agent`
- Supporting agents: `test-agent`, `security-agent`
- Use when: resolving static-analysis findings without changing business behavior

### docs-update

- Read: `.ai/task-types.md`, `.ai/agents/docs-agent.md`, `.ai/skills/docs-update/SKILL.md`
- Lead agent: `docs-agent`
- Supporting agents: `architect-agent`, `backend-agent`, `reviewer-agent`
- Use when: updating framework, architecture, API, or business-flow docs

### code-review

- Read: `.ai/task-types.md`, `.ai/agents/reviewer-agent.md`, `.ai/skills/code-review/SKILL.md`
- Lead agent: `reviewer-agent`
- Supporting agents: `security-agent`, `sql-agent`, `test-agent`, `architect-agent`
- Use when: reviewing pending changes for correctness and risk

### security-sensitive change

- Read: `.ai/task-types.md`, `.ai/agents/security-agent.md`, plus the main domain agent and skill
- Lead agents: `security-agent` and the implementation-domain agent
- Supporting agents: `architect-agent`, `test-agent`, `reviewer-agent`
- Use when: recipient data, message content, secrets, auth, audit, or destructive ops are involved
