# PostgreSQL MCP Guidance

## Purpose

PostgreSQL MCP can be used to inspect schema, examine tables and indexes, and run read-only investigation queries for development or local environments.

## Default Safety Rules

- PostgreSQL MCP must be read-only by default.
- Prefer a dedicated read-only database user.
- Never use production database credentials.
- Never use application admin or superuser credentials.
- Never hardcode database credentials in repository files.
- Prefer environment variables or local wrapper scripts for connection strings.
- Do not paste secrets, credentials, or sensitive customer data into responses.

## Codex `config.toml` Example

Use a local stdio MCP server with a read-only connection string:

```toml
[mcp_servers.Postgres]
command = "npx"
args = ["-y", "@modelcontextprotocol/server-postgres", "postgresql://READONLY_USER:READONLY_PASSWORD@localhost:5432/YOUR_DATABASE"]
startup_timeout_sec = 20
tool_timeout_sec = 60
default_tools_approval_mode = "prompt"
```

## Safer Wrapper Script Approach For Windows

Avoid storing credentials directly in `config.toml`. Prefer a local wrapper script outside the repository that reads environment variables.

Example `run-postgres-mcp.cmd`:

```text
@echo off
set "PG_MCP_URL=postgresql://%PG_RO_USER%:%PG_RO_PASSWORD%@localhost:5432/%PG_DATABASE%"
npx -y @modelcontextprotocol/server-postgres "%PG_MCP_URL%"
```

Then point Codex at that wrapper:

```toml
[mcp_servers.Postgres]
command = "C:\\path\\to\\run-postgres-mcp.cmd"
startup_timeout_sec = 20
tool_timeout_sec = 60
default_tools_approval_mode = "prompt"
```

## Read-Only User Example

Example SQL to create a read-only PostgreSQL user:

```sql
CREATE USER readonly_user WITH PASSWORD 'replace_me_locally';
GRANT CONNECT ON DATABASE your_database TO readonly_user;
GRANT USAGE ON SCHEMA public TO readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO readonly_user;
```

Apply real user names, passwords, schemas, and database names outside repository files.

## Allowed Usage

- Inspect schema, tables, views, indexes, and constraints
- Run `SELECT`-only queries on local or development databases
- Investigate SQL and performance behavior in read-only mode

## Approval Rules

Explicit approval is required before:

- Any `INSERT`, `UPDATE`, `DELETE`, `MERGE`, `COPY`, DDL, or write-capable procedure call
- Accessing staging or production databases
- Exporting data
- Querying sensitive personal or financial data
