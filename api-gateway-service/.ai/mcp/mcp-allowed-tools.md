# MCP Allowed Tools

## Safe Tool Usage

Allowed by default when relevant to the task:

- Read repository files and directories
- Search code, configuration, SQL, and documentation
- Run non-destructive build, test, lint, and static-analysis commands
- Inspect git status and diffs
- Generate documentation and repository guidance
- Run safe local verification scripts such as `scripts/agent-check.sh`
- Read GitLab project metadata
- Read GitLab issues and merge requests
- Read GitLab repository metadata
- Inspect PostgreSQL schema
- Run `SELECT`-only queries on development or local PostgreSQL databases

## Preferred Action Style

- Prefer read-only and local actions
- Prefer commands that do not modify external systems
- Prefer focused verification over broad, risky execution
- Stop when required tools are missing and report the gap clearly
- Summarize only the minimum MCP output needed to answer the task
- Redact or omit secrets, credentials, tokens, and sensitive customer data

## Read-Only Guidance

Read-only usage is strongly preferred for:

- Databases
- Production-like environments
- Secrets stores
- Deployment tools
- Payment and financial system integrations
- GitLab state changes
