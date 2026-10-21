# Self-Healing Rules

## What is self-healing in this framework?

Self-healing in this framework means making tests **resilient to minor, non-breaking API changes**
without requiring immediate code changes. It does NOT mean silently ignoring real regressions.

## Self-healing strategies implemented

### 1. AI Advisory (non-breaking)
When `agr.ai.enabled=true`, `ApiResponseVerifier.adviseOnMismatch()` classifies field
mismatches as COSMETIC, ADDITIVE, or BREAKING. This helps decide:
- **COSMETIC**: update the assertion locally (test fix)
- **ADDITIVE**: ignore the extra field (test is fine)
- **BREAKING**: escalate to API team (real regression)

### 2. Fail-Open Mode (`agr.ai.fail-open=true`)
AI analysis failures never break the test suite. Tests pass/fail based solely
on deterministic assertions, not AI output.

### 3. Budget Guard (`agr.ai.budget.max-calls-per-run=20`)
Prevents runaway AI calls if many tests fail simultaneously in CI.

## What self-healing does NOT mean

❌ Tests should NOT silently catch and swallow assertion errors  
❌ Tests should NOT automatically update expected values  
❌ AI output should NOT change test pass/fail outcomes  
❌ Tests should NOT retry on assertion failures (only on connection failures)  

## When to add retry logic

Only use retries for `TIMING` category failures:
```java
// Example: retry pattern for eventual consistency
Awaitility.await()
    .atMost(10, TimeUnit.SECONDS)
    .pollInterval(1, TimeUnit.SECONDS)
    .until(() -> petService.getPetById(id).statusCode() == 200);
```

## Updating tests vs fixing the API

| AI says BREAKING | → Open a bug. Don't update the test. |
|---|---|
| AI says ASSERTION | → Update the hardcoded expected value in the test. |
| AI says COSMETIC | → Either normalize the assertion or accept the format. |
| AI says DATA_SETUP | → Fix the @BeforeMethod or test data creation. |
