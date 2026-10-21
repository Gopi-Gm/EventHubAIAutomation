# AGR Change Impact Skill

## Purpose
Use this skill to understand the blast radius of an API or test code change before
merging or deploying. It maps changed files/scenarios to potentially affected test scenarios
and identifies regression risk areas.

## When to activate
- A service class, POJO, or endpoint URL is being changed
- You want to know which tests cover a modified API path
- Preparing a PR and want to identify what to re-run

## What this skill does

1. Reads the changed files/endpoints provided by the user (or description)
2. Maps changes to feature files, step definitions, and service classes that are affected
3. Identifies scenarios likely to regress based on the change scope
4. Produces a prioritized re-run list

## How to use
Provide the skill with:
- The file(s) or endpoint(s) being changed
- A brief description of the change (e.g., "rename status field to petStatus")

The skill will:
- Identify all test scenarios that call the affected endpoint
- Flag assertions that reference the changed field
- Suggest whether to update the test or the schema

## Project service map
| Service | Endpoint area | Tests |
|---------|--------------|-------|
| `PetService` | `/pet/**` | `PetTests` |
| `BaseService` | All endpoints (base config) | All tests |

## Key files to check on any change
- `src/test/java/com/api/services/` — service layer
- `src/test/java/com/api/models/` — request/response POJOs
- `src/test/java/com/api/tests/` — test classes
- `src/test/resources/` — test configuration
