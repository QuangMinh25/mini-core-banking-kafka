# Sonar Fix Skill

## When To Use

Use for SonarQube or static-analysis remediation where business behavior should remain unchanged.

## Goal

Fix the reported issue with the lowest-risk change while preserving runtime behavior.

## Required Workflow

1. Understand the exact Sonar issue and why it was raised.
2. Check whether the warning reflects a real defect, maintainability issue, or false positive.
3. Apply the smallest safe remediation.
4. Verify that behavior has not changed.
5. Report the issue type, fix approach, and verification performed.

## Checklist

- Warning understood before changing code
- Fix kept behaviorally neutral where possible
- Risk of side effects reviewed
- Verification result captured

## Common Mistakes To Avoid

- Large refactors for small static-analysis findings
- Silencing warnings without understanding them
- Changing business logic while fixing maintainability issues

## Completion Requirements

The result must include the issue addressed, the low-risk fix applied, verification result, and any cases needing manual review.
