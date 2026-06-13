# Business Flow

## Observed Runtime Flow

Based on the checked-in code, the only confirmed runtime flow is:

1. `ApiGatewayServiceApplication.main(...)` starts Spring Boot.
2. Spring loads `application.yaml`.
3. The application context initializes.
4. `ApiGatewayServiceApplicationTests.contextLoads()` verifies that startup succeeds in test scope.

## Gateway Request Flow

No concrete request-routing flow is implemented in this module yet.

- Incoming route predicates: `Unknown / needs confirmation`
- Filter chain behavior: `Unknown / needs confirmation`
- Downstream service mapping: `Unknown / needs confirmation`
- Circuit-breaker behavior: `Unknown / needs confirmation`
- Auth or token propagation flow: `Unknown / needs confirmation`

## Development Workflow For This Module

1. Read `AGENTS.md`.
2. Read `.ai/project-profile.md` and `.ai/agent-router.md`.
3. Load only the task-relevant agent and skill files.
4. Inspect `build.gradle`, `application.yaml`, and the affected code paths.
5. Verify with `bash scripts/agent-check.sh` when code changes are made.

## Notes

Because this service is still a scaffold, many business and routing details are intentionally undocumented here rather than invented.
