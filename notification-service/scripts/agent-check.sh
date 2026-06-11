#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

overall_status=0
checks_run=0

log() {
  printf '[agent-check] %s\n' "$1"
}

run_check() {
  local label="$1"
  shift

  checks_run=$((checks_run + 1))
  log "Running: ${label}"

  if "$@"; then
    log "OK: ${label}"
  else
    log "FAILED: ${label}"
    overall_status=1
  fi
}

log "Starting safe verification checks in: ${PROJECT_ROOT}"

if [[ -f "$PROJECT_ROOT/build.gradle" || -f "$PROJECT_ROOT/build.gradle.kts" ]]; then
  if [[ -f "$PROJECT_ROOT/gradlew" ]]; then
    run_check "Gradle test" bash "$PROJECT_ROOT/gradlew" --no-daemon test
  elif command -v gradle >/dev/null 2>&1; then
    run_check "Gradle test" gradle --no-daemon test
  else
    log "Gradle project detected but neither 'gradlew' nor 'gradle' is available."
    overall_status=1
  fi
else
  log "No Gradle build file detected. No verification commands were run."
fi

log "Verification summary:"
log "Checks attempted: ${checks_run}"

if [[ "$overall_status" -eq 0 ]]; then
  log "Overall result: SUCCESS"
else
  log "Overall result: FAILED"
fi

exit "$overall_status"
