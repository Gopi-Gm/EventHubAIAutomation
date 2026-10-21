# AGR Test Doctor Skill — AI Failure Analyzer

## Purpose
Use this skill to diagnose and explain test failures using AI. When a test fails,
the Test Doctor runs root cause analysis (RCA), classifies the failure, and suggests
actionable fixes — without changing test outcomes.

## When to activate
- A test has just failed and the error is unclear
- You see a pattern of failures across multiple tests
- A CI run failed and you need rapid triage
- You want a second opinion before raising a bug or updating a test

## What this skill does

1. Reads the failed test name, error message, and stack trace
2. Builds a structured prompt using `failure-rca.md`
3. Sends to `AiOrchestrator.analyzeFailure()` (or equivalent)
4. Returns structured JSON: category, summary, likelyCause, suggestedChecks

## Failure Categories
| Category      | Description |
|---------------|-------------|
| AUTH          | 401/403, expired or missing token |
| DATA_SETUP    | Missing/stale test data, incorrect pre-conditions |
| API_CONTRACT  | Response field names, types, or values differ from contract |
| ASSERTION     | Test assertion is wrong, outdated, or overly strict |
| ENVIRONMENT   | Network issue, server down, config mismatch |
| TIMING        | Race condition, eventual consistency, timeout |
| OTHER         | Unclassifiable — needs manual investigation |

## How to use manually
Pass a failure description to the AI orchestrator:
```java
FailureContext ctx = new FailureContext(
    "testMethodName",       // scenario
    "com.api.tests.PetTests", // feature/class URI
    e.getMessage(),         // error message
    FailureContext.abbreviateStackTrace(e, 15) // stack trace
);
Optional<FailureAnalysisResult> result = AiOrchestrator.analyzeFailure(ctx);
```

## Automatic activation
When `agr.ai.enabled=true`, `AiReportEnhancer` automatically triggers this
skill for every TestNG test failure. See `target/ai-analysis/ai-dashboard.html`
after the run.

## Reference knowledge
This skill uses project-specific knowledge from the `references/` directory:
- [`framework-standards.md`](./references/framework-standards.md)
- [`api-testing-rules.md`](./references/api-testing-rules.md)
- [`assertion-rules.md`](./references/assertion-rules.md)
- [`failure-classification.md`](./references/failure-classification.md)
- [`self-healing-rules.md`](./references/self-healing-rules.md)
- [`rest-assured-patterns.md`](./references/rest-assured-patterns.md)
- [`test-data-rules.md`](./references/test-data-rules.md)
- [`known-api-behaviors.md`](./references/known-api-behaviors.md)
