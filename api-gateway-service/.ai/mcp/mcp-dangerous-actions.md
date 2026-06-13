# MCP Dangerous Actions

## Explicit Approval Required

The following actions require explicit user approval before execution:

- Database write operations, migrations against shared environments, or data fixes
- Git push, branch deletion, rebase, reset, revert, or history rewriting
- Any deployment or runtime restart outside local development
- Secret retrieval, rotation, or modification
- Calls to real payment, transfer, settlement, or financial transaction systems
- Webhook replay against non-local environments
- Bulk file changes outside the task scope
- Create, update, or delete GitLab issues, merge requests, comments, branches, tags, labels, or other GitLab state
- Access staging or production databases
- Export data
- Query sensitive personal or financial data

## Forbidden By Default

The following actions should not be performed unless the user gives a highly specific instruction and the action is clearly necessary:

- Dropping tables, truncating data, or destructive schema changes
- Force-push and destructive git cleanup
- Production data extraction for convenience
- Disabling authentication, authorization, audit logging, or security checks
- Running unknown shell scripts from untrusted sources
- Production database writes
- Using application admin or superuser database credentials
- Logging or committing secrets, tokens, or credentials
- Destructive GitLab operations without approval

## Extra Rules For Sensitive Domains

- Financial and core banking tasks require strict auditability and least privilege.
- If the task touches money movement, customer identity, or approval workflows, involve `security-agent` and document risk explicitly.
- When in doubt, stop and request approval instead of assuming access is acceptable.
