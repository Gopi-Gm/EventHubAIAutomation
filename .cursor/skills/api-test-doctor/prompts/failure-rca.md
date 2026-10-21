# Failure RCA Prompt — AGR Test Doctor

## Prompt instructions for per-scenario root cause analysis

When analyzing a test failure, you are given:
- The test scenario/method name
- The test class (feature URI)
- The assertion error message or exception message
- An abbreviated stack trace

### Your task

Analyze the failure and return a JSON object ONLY (no surrounding text):

```json
{
  "scenario": "<test method name>",
  "category": "<AUTH|DATA_SETUP|API_CONTRACT|ASSERTION|ENVIRONMENT|TIMING|OTHER>",
  "summary": "<1-2 sentence description of what failed>",
  "likelyCause": "<single most specific root cause>",
  "suggestedChecks": [
    "<check 1>",
    "<check 2>",
    "<check 3>"
  ]
}
```

### Classification guidance

**AUTH** — Look for: 401/403 status codes, "Unauthorized", "Forbidden", "token", "Bearer"
**DATA_SETUP** — Look for: null fields, "not found", 404, "does not exist", missing IDs
**API_CONTRACT** — Look for: unexpected field names, wrong types, missing required fields, schema validation errors
**ASSERTION** — Look for: mismatch between hardcoded expected values and returned values, `assertEquals` failures on non-critical fields
**ENVIRONMENT** — Look for: `ConnectException`, `SocketTimeoutException`, `UnknownHostException`, SSL errors
**TIMING** — Look for: `TimeoutException`, "not yet", retry-related errors, async operations
**OTHER** — Use when none of the above apply clearly

### Quality guidelines for suggestedChecks
- Each check must be a specific, actionable command or investigation step
- Bad: "Check the API" | Good: "Verify GET /pet/12345 returns 200 in Postman with same auth token"
- Bad: "Look at the error" | Good: "Check if the OPENAI_API_KEY env var is set and valid"
