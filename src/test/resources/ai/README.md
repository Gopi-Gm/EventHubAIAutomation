# AI Resources — `src/test/resources/ai/`

This directory contains AI prompt templates and JSON schemas used by the
`com.api.ai` package to standardize and document expected AI behaviors.

## Layout

```
src/test/resources/ai/
├── prompts/
│   ├── failure-analysis.txt       # Per-scenario failure RCA prompt
│   ├── api-mismatch-advisory.txt  # Advisory mismatch classification prompt
│   ├── run-summary.txt            # High-level run summary prompt
│   └── scenario-generation.txt   # Gherkin + Java step generation guidance
└── schemas/
    ├── failure-analysis.schema.json  # JSON Schema for FailureAnalysisResult
    └── run-summary.schema.json       # JSON Schema for run summary object
```

## Prompt Templates (`prompts/`)

| File | Purpose |
|------|---------|
| `failure-analysis.txt` | Instructs AI to return per-scenario failure analysis as structured JSON |
| `api-mismatch-advisory.txt` | Instructs AI to classify field mismatches as COSMETIC / ADDITIVE / BREAKING |
| `run-summary.txt` | Guides generation of a 1–3 sentence plain-English run summary |
| `scenario-generation.txt` | Guides AI when drafting new Gherkin scenarios and Java step signatures |

> **Note:** The Java runtime currently uses embedded prompt strings in `AiOrchestrator`.
> These `.txt` files serve as documentation and are ready to be loaded at runtime
> for future prompt externalisation.

## JSON Schemas (`schemas/`)

| File | Validates |
|------|-----------|
| `failure-analysis.schema.json` | `FailureAnalysisResult` — enforces all required fields and category enum |
| `run-summary.schema.json` | Run summary object — enforces `{ "summary": string }` shape |

These schemas capture the contract between the AI response and the Java POJOs,
making it easy to validate AI output in tests or CI.
