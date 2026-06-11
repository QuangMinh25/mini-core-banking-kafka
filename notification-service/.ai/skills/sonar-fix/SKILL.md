# Sonar Fix Skill

## When To Use

Use for static-analysis remediation in `notification-service` when business behavior should stay unchanged.

## Goal

Fix the reported issue with the lowest-risk change and honest verification.

## Required Workflow

1. Understand the exact warning and affected file.
2. Confirm whether it reflects a real defect, maintainability issue, or false positive.
3. Apply the smallest safe remediation.
4. Re-run the safest relevant verification.
5. Report issue type, fix approach, and residual risk.

## Common Mistakes To Avoid

- Large refactors for a narrow finding
- Claiming behavior is unchanged without evidence
- Ignoring Spring, Kafka, or SQL side effects

## Completion Requirements

Include the issue addressed, the low-risk fix, verification result, and any manual-review needs.
