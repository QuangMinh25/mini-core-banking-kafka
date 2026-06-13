# Skill: Coding Discipline

## When to use

Use this skill for any task that modifies code, especially:

* bug fixes
* new features
* refactoring
* SQL changes
* performance changes
* security-sensitive changes
* SonarQube fixes
* large or ambiguous tasks

For very small and obvious changes, apply the core principles from this skill without loading extra context.

## Goal

Help the AI agent make safe, minimal, correct, and verifiable changes.

The agent must avoid guessing, overengineering, unrelated refactors, and changes that cannot be traced back to the task.

## Core Principles

### 1. Think before coding

Before editing code, understand:

* the user request
* the expected behavior
* the current behavior
* the affected files
* the likely root cause or implementation area
* the risks and assumptions

If the requirement is unclear, state assumptions or ask for clarification before making risky changes.

### 2. Simplicity first

Prefer the simplest solution that satisfies the task.

Do not add unnecessary abstractions, frameworks, dependencies, design patterns, or generic utilities.

Avoid making the code "more beautiful" if the task only requires a small fix.

### 3. Surgical changes

Only touch files directly related to the task.

Every changed line should have a clear reason connected to the user request.

Do not rewrite unrelated code, rename unrelated classes, change public contracts, or refactor surrounding logic unless required for correctness or safety.

### 4. Goal-driven execution

Define what "done" means before finishing.

A task is not complete until the change is checked against the acceptance criteria, build/test result, or another clear verification method.

Do not claim success without evidence.

## Required Workflow

1. Read `AGENTS.md`.
2. Read `.ai/project-profile.md` if project context is needed.
3. Read `.ai/agent-router.md` or `.ai/context-map.md` if available.
4. Load only the task-relevant skill and agent files.
5. Do not read the entire `.ai/` directory by default.
6. Identify the exact task type and expected outcome.
7. Inspect the current implementation before editing.
8. State important assumptions if any.
9. Make the smallest safe change.
10. Avoid unrelated refactors.
11. Run the safest available verification command, usually `scripts/agent-check.sh`, when feasible.
12. Report the result clearly.

## Checklist

Before changing code:

* [ ] Is the requirement clear?
* [ ] Is the affected flow understood?
* [ ] Are assumptions stated?
* [ ] Are the target files identified?
* [ ] Is the planned change minimal?

While changing code:

* [ ] Did I avoid unrelated files?
* [ ] Did I preserve existing architecture?
* [ ] Did I preserve public API behavior unless required?
* [ ] Did I avoid unnecessary dependencies or abstractions?
* [ ] Did I avoid weakening validation, security, or error handling?

Before finishing:

* [ ] Did I run verification if feasible?
* [ ] Did I explain skipped verification if not feasible?
* [ ] Did I list changed files?
* [ ] Did I mention risks or follow-up items?
* [ ] Can every change be traced back to the task?

## Common Mistakes To Avoid

* Starting to code before understanding the flow.
* Guessing business rules.
* Making broad refactors for a small task.
* Adding abstractions before they are needed.
* Changing API contracts accidentally.
* Hiding uncertainty instead of stating assumptions.
* Swallowing exceptions to make errors disappear.
* Adding logs that expose sensitive data.
* Claiming tests passed without running them.
* Reading the entire `.ai/` folder when only one skill is needed.

## Completion Requirements

The final response must include:

### Summary

Briefly explain what was changed or analyzed.

### Changed files

List every file created or modified.

### Verification result

State exactly what was run and what happened.

If verification was not run, explain why.

### Risks / notes

List assumptions, remaining risks, skipped checks, or follow-up items.
