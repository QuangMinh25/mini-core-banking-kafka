# Code Review Skill

## When To Use

Use for reviewing current uncommitted or proposed changes.

## Goal

Find meaningful correctness, security, SQL, regression, and maintainability issues, then classify them clearly by severity.

## Required Workflow

1. Review the change scope and affected files.
2. Check correctness before style.
3. Examine security, data, transaction, and verification risks.
4. Classify findings by severity and confidence.
5. Report open questions and residual risks if no definitive issue is found.

## Checklist

- Findings focused on real risk
- Severity classification provided
- Missing tests or verification called out
- Scope creep or unrelated changes identified

## Common Mistakes To Avoid

- Prioritizing style comments over correctness defects
- Missing hidden behavior changes
- Treating absence of tests as acceptable without noting risk

## Completion Requirements

The review must include findings by severity, affected files or areas, verification gaps, and residual risks even when no blocking issue is found.
