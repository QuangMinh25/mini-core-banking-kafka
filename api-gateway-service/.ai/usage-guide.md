# Usage Guide

## Default Loading Pattern

Always start with:

1. `AGENTS.md`
2. `.ai/project-profile.md`
3. `.ai/agent-router.md`

Then read only the task-relevant agent, skill, MCP, and doc files.

Do not read the entire `.ai/` directory by default.

## Codex Desktop App

```text
Read AGENTS.md first. Then read .ai/project-profile.md and .ai/agent-router.md. Load only the task-relevant skill and agent files for api-gateway-service. Do not read the whole .ai folder. Make the smallest safe change, run bash scripts/agent-check.sh if relevant, or use gradlew.bat --no-daemon test on Windows when bash is unavailable, and report using the required completion format.
```

## Codex CLI

```text
Read AGENTS.md and .ai/project-profile.md first. Then load only the relevant agent and skill files for this api-gateway-service task. Do not assume routes, security rules, or SQL exist if the repository does not show them.
```

## Codex IDE

```text
Use AGENTS.md as the repo instruction file. Follow .ai/agents/docs-agent.md and .ai/skills/docs-update/SKILL.md. Update the docs for api-gateway-service from code and checked-in config only. Mark missing gateway details as Unknown / needs confirmation.
```

## Other AI Coding Tools

```text
This repository uses AGENTS.md as the working contract. Read AGENTS.md, .ai/project-profile.md, and .ai/agent-router.md first. Then load only the task-relevant skill and agent files. Follow the safety rules for full-access mode. Run bash scripts/agent-check.sh only as a non-destructive verification step.
```

## MCP Usage

- Read `.ai/mcp/mcp-policy.md` first.
- Use [`.ai/mcp/mcp-gitlab.md`](/C:/Users/web.quangminh/Desktop/mini-core-banking-kafka/api-gateway-service/.ai/mcp/mcp-gitlab.md) only when GitLab context is needed.
- Use [`.ai/mcp/mcp-postgresql.md`](/C:/Users/web.quangminh/Desktop/mini-core-banking-kafka/api-gateway-service/.ai/mcp/mcp-postgresql.md) only when a DB investigation is justified.
- Prefer read-only MCP usage by default.

## Example Prompts

### Debug Gateway Issue

```text
Read AGENTS.md first. Then read .ai/project-profile.md, .ai/agent-router.md, and .ai/skills/debug/SKILL.md. Use backend-agent, test-agent, and reviewer-agent. Trace gateway startup, route/filter flow, and downstream boundaries before modifying code.
```

### Add Safe Gateway Trace Logs

```text
Read AGENTS.md first. Use .ai/skills/tracelog-performance/SKILL.md and .ai/agents/backend-agent.md. Add useful logs only at gateway entry, route/filter, downstream call, resilience, and error boundaries. Do not log secrets, private data, or full payloads.
```

### Update Docs

```text
Read AGENTS.md first. Use .ai/agents/docs-agent.md and .ai/skills/docs-update/SKILL.md. Update documentation based only on actual code and checked-in config. Mark missing route, security, or integration details as Unknown / needs confirmation.
```
