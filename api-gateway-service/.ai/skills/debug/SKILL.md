# Debug Skill

## When To Use

Use for:

- Runtime exception
- Unexpected API response
- Incorrect business result
- Job failure
- Integration failure
- Transaction rollback issue
- NullPointerException or validation bug
- Environment-specific issue
- Reproducible or intermittent bug

## Goal

- Find root cause safely.
- Avoid random code changes.
- Avoid masking the symptom without fixing the cause.
- Preserve existing behavior unless the bug requires a behavior change.

## Required Workflow

1. Read `AGENTS.md` and the project profile.
2. Understand expected behavior.
3. Reproduce the issue or identify reproduction steps.
4. Locate the entry point.
5. Trace the flow from bootstrap or gateway entry through route/filter/resilience/downstream boundaries to the response or output.
6. Identify the logs, exceptions, stack traces, SQL, input data, and configuration involved.
7. Form a hypothesis.
8. Verify the hypothesis with code reading, logs, tests, or safe local checks.
9. Apply a minimal fix only after the root cause is clear.
10. Add or update a test if practical.
11. Run the verification harness.
12. Report root cause, fix, changed files, verification, and risks.

## Debug Checklist

- Input validation checked
- Null handling checked
- Exception handling checked
- Transaction boundary checked
- SQL parameters checked
- Date, time, and month formatting checked
- Route match, filter order, downstream call, or message boundary checked if relevant
- Config, profile, and environment checked
- Logs checked
- Regression risk checked

## Common Mistakes To Avoid

- Guessing without tracing the flow
- Changing unrelated code
- Swallowing exceptions
- Adding logs with sensitive data
- Fixing only the symptom
- Disabling validation or security checks
- Changing transaction behavior without understanding impact

## Completion Requirements

- Root cause summary
- Fix summary
- Changed files
- Verification result
- Remaining risks
