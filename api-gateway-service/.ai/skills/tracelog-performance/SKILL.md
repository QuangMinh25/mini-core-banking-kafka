# Trace Log And Performance Skill

## When To Use

Use for:

- Slow API
- Slow SQL
- Slow batch job
- Timeout
- JDBC timeout
- External integration timeout
- High memory or CPU suspicion
- Too many DB calls
- N+1 query suspicion
- Need to add trace logs
- Need to improve observability
- Need to track request flow across services

## Goal

- Identify performance bottlenecks safely.
- Improve observability without exposing secrets.
- Add useful trace logs only where needed.
- Avoid noisy logging.
- Avoid changing business behavior.
- Recommend performance improvements based on evidence.

## Required Workflow

1. Read `AGENTS.md` and the project profile.
2. Identify the slow flow or trace target.
3. Map the request or job flow.
4. Identify the entry point and exit point.
5. Identify route matches, filter execution, resilience wrappers, external calls, loops, batch size, and transaction boundaries.
6. Check existing logging style and correlation ID usage if any.
7. Check existing tracing, MDC, requestId, or traceId conventions if any.
8. Propose minimal trace points.
9. Add logs only at useful boundaries:
   - request or gateway entry
   - route match or filter boundary
   - downstream service call boundary
   - external integration boundary
   - error path
   - request or job end with elapsed time
10. Never log secrets, tokens, passwords, private data, full payloads, or financial sensitive details.
11. For performance investigation, prefer measuring elapsed time before optimizing.
12. If SQL is involved, route to `sql-agent` and `sql-change` skill as needed.
13. If the task is security-sensitive, route to `security-agent`.
14. Run the verification harness.

## Trace Log Checklist

- requestId, traceId, or correlationId checked
- Log level selected correctly
- No sensitive data logged
- Start and end logs added only where useful
- Elapsed time captured where useful
- Error logs include context but not secrets
- Existing logging style preserved
- Log noise avoided

## Performance Checklist

- Slow SQL checked
- Missing index risk noted
- N+1 query risk checked
- Unnecessary loop DB calls checked
- Large object or list memory risk checked
- External timeout checked
- Retry behavior checked
- Transaction duration checked
- Batch size checked
- Pagination checked
- Caching opportunity noted if safe
- Async or message queue opportunity noted if relevant

## Common Mistakes To Avoid

- Adding too many logs
- Logging full request or response payloads
- Logging secrets or personal data
- Optimizing without measurement
- Changing SQL business meaning
- Adding cache without an invalidation strategy
- Increasing timeout without finding root cause
- Making broad refactors during investigation

## Completion Requirements

- Flow traced
- Suspected bottleneck
- Evidence found
- Changes made, if any
- Logs added, if any
- Performance risks
- Verification result
- Recommended next steps
