# AGR API Response Verifier Skill

## Purpose
Use this skill to perform AI-powered advisory analysis on API response field mismatches.
It does NOT change test pass/fail — it adds semantic insight to help developers distinguish
between a broken test and a real API regression.

## When to activate
- A test assertion on a response field fails
- You want to know if a mismatch is COSMETIC, ADDITIVE, or BREAKING
- You want actionable guidance before deciding to fix the test or escalate to the API team

## How to invoke
```java
// In your test, after catching an AssertionError:
ApiResponseVerifier.adviseOnMismatch("fieldName", expected, actual, response.asString());
```

## Output
Logs AI advisory to console with:
- `category`: COSMETIC | ADDITIVE | BREAKING
- `summary`: what the mismatch means
- `likelyCause`: single most likely reason
- `suggestedChecks`: 3 actionable next steps

## Category definitions
| Category  | Meaning |
|-----------|---------|
| COSMETIC  | Formatting only (case, whitespace). Functionally identical — update the test. |
| ADDITIVE  | API returned more than expected — new optional field. Likely safe to ignore. |
| BREAKING  | Semantic value change — real API regression, escalate to API team. |

## Requirements
- `agr.ai.enabled=true` in `test-config.properties`
- `OPENAI_API_KEY` environment variable set

## Reference
See [reference.md](./reference.md) for REST Assured assertion patterns.
