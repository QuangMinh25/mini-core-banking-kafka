# Issue Analysis Skill

## When To Use

Use when the task is investigation only and no code should be modified.

## Goal

Understand the issue in `banking-core-service`, identify related files and probable causes, and report uncertainty clearly.

## Required Workflow

1. Read the default context files only.
2. Inspect the relevant source, config, test, build, and docs files.
3. Trace whether the issue is local to this service or involves `notification-service`, PostgreSQL, or Kafka.
4. Separate evidence from inference.
5. Report findings without changing files.

## Common Mistakes To Avoid

- Presenting likely banking behavior as confirmed behavior
- Ignoring cross-service or infrastructure context when the issue mentions messaging or persistence
- Quietly editing docs or code during analysis

## Completion Requirements

Include likely cause, related files, confidence level, open questions, and recommended next step.
