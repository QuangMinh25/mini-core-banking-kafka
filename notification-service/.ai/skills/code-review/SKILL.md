# Code Review Skill

## When To Use

Use for reviewing pending changes in `notification-service`.

## Goal

Find correctness, security, SQL, regression, and documentation risks before style concerns.

## Required Workflow

1. Review the change scope and affected files.
2. Check whether claims about APIs, flows, schema, Kafka, or security are supported by code.
3. Examine verification coverage and risk notes.
4. Classify findings by severity and confidence.
5. Report residual risks even when no blocking defect is found.

## Common Mistakes To Avoid

- Focusing on formatting while missing behavioral risk
- Ignoring unsupported assumptions in docs or code
- Skipping cross-service implications

## Completion Requirements

Include findings by severity, affected files or areas, verification gaps, and residual risks.
