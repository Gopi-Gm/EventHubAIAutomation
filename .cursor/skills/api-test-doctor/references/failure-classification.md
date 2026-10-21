# Failure Classification Guide

## How to classify a test failure

Use this guide when triaging failures — either manually or as context for AI classification.

## Classification Tree

```
Test failed?
│
├─ Is it a connection error / timeout / SSL error?
│   → ENVIRONMENT
│
├─ Is the status code 401 or 403?
│   → AUTH
│
├─ Is the status code 404 and the test expects an existing resource?
│   → DATA_SETUP (test data was cleaned up or never created)
│
├─ Is the status code unexpected (e.g., 500, 400 when 200 was expected)?
│   ├─ 500 → ENVIRONMENT (server-side error)
│   └─ 400 → API_CONTRACT (bad request — payload or field name changed)
│
├─ Status code is correct (200) but body assertion fails?
│   ├─ Field name changed (e.g., "name" → "petName") → API_CONTRACT
│   ├─ Field value changed semantically → API_CONTRACT or DATA_SETUP
│   ├─ Expected value is hardcoded and stale → ASSERTION
│   └─ Field ordering / formatting different → ASSERTION (test is too strict)
│
├─ Test intermittently passes and fails?
│   → TIMING (race condition, eventual consistency)
│
└─ None of the above → OTHER
```

## Category decision rules

### AUTH vs DATA_SETUP
- **AUTH**: Status code 401/403. Token is invalid, expired, or not sent.
- **DATA_SETUP**: Status code 404. The resource ID being tested doesn't exist.

### API_CONTRACT vs ASSERTION
- **API_CONTRACT**: The API changed — field added, removed, renamed, or type changed.
- **ASSERTION**: The test is wrong — hardcoded expected value is outdated, or assertion is too strict.

### ENVIRONMENT vs TIMING
- **ENVIRONMENT**: Failure is consistent across retries. Server is down, DNS fails.
- **TIMING**: Failure passes on retry. Data hasn't propagated yet. Use with eventual-consistency APIs.

## Severity
| Category | Severity | Action |
|---|---|---|
| AUTH | High | Fix token handling immediately |
| API_CONTRACT | High | Raise bug or update test + schema |
| DATA_SETUP | Medium | Fix test data setup/teardown |
| ASSERTION | Low-Medium | Update test assertion |
| ENVIRONMENT | High | Investigate infrastructure |
| TIMING | Medium | Add retry or adjust wait strategy |
| OTHER | Unknown | Manual investigation required |
