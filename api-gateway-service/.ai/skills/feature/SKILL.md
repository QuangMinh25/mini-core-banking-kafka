# Feature Skill

## When To Use

Use for adding new functionality or expanding existing behavior.

## Goal

Implement the feature safely with clear scope, acceptance alignment, verification, and documentation updates.

## Required Workflow

1. Confirm requirements and acceptance criteria.
2. Use architecture guidance to define boundaries and impact.
3. Implement in the appropriate gateway layers with minimal unrelated change.
4. Add or update tests and documentation where needed.
5. Run safe verification commands and summarize results.

## Checklist

- Scope and acceptance criteria understood
- Affected modules identified
- Implementation follows existing patterns
- Gateway routes, filters, downstream contracts, and security impact checked when relevant
- Tests and docs updated where relevant
- Verification outcome reported honestly

## Common Mistakes To Avoid

- Starting implementation without boundary analysis
- Shipping feature logic without docs or tests
- Mixing cleanup refactors into feature work

## Completion Requirements

The result must include feature scope, implemented behavior, changed files, verification result, and follow-up notes.
