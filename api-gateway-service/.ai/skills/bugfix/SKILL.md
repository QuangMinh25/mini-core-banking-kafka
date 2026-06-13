# Bugfix Skill

## When To Use

Use for correcting an existing defect with the smallest safe behavior change.

## Goal

Find the root cause, apply a minimal safe fix, and reduce regression risk.

## Required Workflow

1. Read `AGENTS.md`, project context, and the relevant agent role files.
2. Confirm the defect scope and likely root cause.
3. Inspect nearby code paths and dependencies.
4. Apply the smallest safe correction.
5. Run focused verification and broader safe checks when appropriate.
6. Report root cause, fix summary, verification result, and remaining risks.

## Checklist

- Root cause identified or clearly narrowed
- Fix limited to necessary scope
- Related edge cases considered
- Verification actually run and reported accurately

## Common Mistakes To Avoid

- Fixing symptoms without understanding the cause
- Refactoring unrelated code during a bugfix
- Claiming regression safety without checks

## Completion Requirements

The result must include root cause, minimal fix summary, changed files, actual verification result, and residual risk notes.
