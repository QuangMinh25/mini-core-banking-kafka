# SQL Change Skill

## When To Use

Use for SQL query edits, repository SQL updates, migrations, or database logic changes.

## Goal

Produce a correct, safe, and performance-aware SQL change.

If no persistence code exists in this service, stop and report that the requested SQL change may belong elsewhere.

## Required Workflow

1. Identify the exact SQL units affected.
2. Validate syntax, parameters, joins, filters, and expected row shape.
3. Check null handling, date handling, and pagination or ordering rules.
4. Review performance risks such as full scans or accidental row explosion.
5. Run safe verification steps and document residual database risks.

## Checklist

- Syntax reviewed
- Named parameters aligned
- Join conditions verified
- Null and date handling considered
- Performance risk noted

## Common Mistakes To Avoid

- Mismatched named parameters
- Incorrect join type or join condition
- Ignoring null semantics and timezone effects
- Treating unbounded queries as safe

## Completion Requirements

The result must include SQL areas changed, correctness checks, performance notes, verification result, and remaining risks.
