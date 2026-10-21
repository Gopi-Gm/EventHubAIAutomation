# EventHub AI Automation Framework

A robust, scalable, and intelligent API automation framework built with Java 11, REST Assured, Cucumber BDD, TestNG, and integrated with an AI-driven test analysis layer powered by OpenAI.

---

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [AI Implementation & Architecture](#ai-implementation--architecture)
  - [Overview](#overview)
  - [Key AI Capabilities](#key-ai-capabilities)
  - [AI Architecture & Components](#ai-architecture--components)
  - [Safety, Privacy & Guardrails](#safety-privacy--guardrails)
  - [Configuration & Execution](#configuration--execution)
  - [AI Output Artifacts](#ai-output-artifacts)
- [Framework Implementation Details](#framework-implementation-details)
- [Running Tests](#running-tests)
- [Reporting](#reporting)

---

## Technology Stack

* **Language:** Java 11
* **API Automation:** REST Assured (v5.3.0)
* **BDD Framework:** Cucumber (v7.18.0)
* **Test Runner & Assertions:** TestNG (v7.10.2)
* **AI & LLM Integration:** OpenAI Chat Completions API (`gpt-4o-mini`) via native Java 11 `HttpClient`
* **Data Binding & Serialization:** Jackson (v2.18.2)
* **Reporting:** AI HTML Dashboard, Allure Report (v2.24.0), ExtentReports (v5.1.1), Cucumber HTML/JSON Reports
* **Logging:** Log4j2 (v2.20.0)
* **Build Tool:** Maven

---

## Project Structure

```text
EventHubAIAutomation
├── src
│   └── test
│       ├── java
│       │   └── com
│       │       └── api
│       │           ├── ai                     # AI Test Intelligence Layer
│       │           │   ├── analysis           # Failure categorization, result models & run summaries
│       │           │   │   ├── FailureAnalysisResult.java
│       │           │   │   ├── FailureAnalyzer.java
│       │           │   │   ├── FailureCategory.java
│       │           │   │   └── RunSummaryGenerator.java
│       │           │   ├── client             # Lightweight Java 11 native OpenAI HTTP client
│       │           │   │   └── OpenAiClient.java
│       │           │   ├── config             # AI configuration and property resolver
│       │           │   │   └── AiConfig.java
│       │           │   ├── orchestration      # Central failure workflow coordinator
│       │           │   │   ├── AiOrchestrator.java
│       │           │   │   └── FailureContext.java
│       │           │   ├── report             # AI HTML dashboard & report listener
│       │           │   │   ├── AiDashboardWriter.java
│       │           │   │   └── AiReportEnhancer.java
│       │           │   ├── runtime            # API call budget and rate controller
│       │           │   │   └── AiRuntimeBudget.java
│       │           │   ├── security           # Redaction of Bearer tokens, secrets, & keys
│       │           │   │   └── AiPromptSanitizer.java
│       │           │   └── verify             # Advisory semantic response validation
│       │           │       └── ApiResponseVerifier.java
│       │           ├── base                   # Service wrappers (Auth, Booking, Event, Health)
│       │           ├── context                # BDD ScenarioContext state sharing
│       │           ├── filters                # REST Assured request/response logging filters
│       │           ├── hooks                  # Cucumber scenario hooks
│       │           ├── listeners              # TestNG test lifecycle listeners
│       │           ├── models                 # Jackson POJO request and response models
│       │           ├── runners                # TestNG Cucumber Runner
│       │           ├── steps                  # Cucumber step definitions
│       │           └── utility                # Assertion, Data, JSON, and Config utilities
│       └── resources
│           ├── ai                             # AI prompt templates and JSON validation schemas
│           │   ├── prompts/                   # failure-analysis, mismatch-advisory, run-summary
│           │   └── schemas/                   # JSON schemas for AI structured responses
│           ├── features/                      # Gherkin BDD feature files
│           ├── log4j2.xml                     # Log4j2 configuration
│           └── test-config.properties         # Framework and AI settings
├── suite.xml                                  # TestNG suite configuration
└── pom.xml                                    # Maven project dependencies and build plugins
```

---

## AI Implementation & Architecture

### Overview

The framework includes a non-intrusive, production-ready **AI Test Intelligence Layer** designed to automate failure triage, analyze root causes, and provide human-readable failure diagnostics.

Rather than replacing deterministic test assertions, the AI layer acts as an **advisory and diagnostic copilot**:
1. When a test fails, the framework automatically intercepts the failure.
2. The failure context (scenario, endpoint, error message, abbreviated stack trace) is sanitized to remove secrets.
3. An LLM (OpenAI `gpt-4o-mini`) generates structured Root Cause Analysis (RCA) and suggested remediation steps.
4. An executive run summary and an interactive HTML dashboard (`target/ai-analysis/ai-dashboard.html`) are produced alongside traditional reports.

```
                      +-----------------------------+
                      |   Test Execution Failure    |
                      +--------------+--------------+
                                     |
                                     v
                       [com.api.ai.report.AiReportEnhancer]
                       (TestNG ITestListener Lifecycle)
                                     |
                                     v
                       [com.api.ai.orchestration.AiOrchestrator]
                      +--------------+---------------+
                      |                              |
                      | 1. Check Budget & Config     v
                      |    [AiConfig / AiRuntimeBudget]
                      | 2. Sanitize Secrets
                      |    [AiPromptSanitizer]
                      | 3. Invoke LLM
                      |    [OpenAiClient]
                      | 4. Parse Structured JSON
                      |    [FailureAnalysisResult / FailureAnalyzer]
                      | 5. Append to Audit Log
                      |    [ai-audit.jsonl]
                      |                              |
                      +--------------+---------------+
                                     |
                                     v
                       +-------------+-------------+
                       |   AI Report & Dashboard   |
                       |  - ai-dashboard.html      |
                       |  - ai-report.json         |
                       +---------------------------+
```

---

### Key AI Capabilities

#### 1. Automated Failure Root Cause Analysis (RCA)
- Triggered automatically on any test failure via [AiReportEnhancer](file:///src/test/java/com/api/ai/report/AiReportEnhancer.java).
- Analyzes scenario details and stack traces, classifying the failure into one of the standardized categories in [FailureCategory](file:///src/test/java/com/api/ai/analysis/FailureCategory.java):
  - `AUTH`: Expired, missing, or unauthorized tokens (401/403).
  - `DATA_SETUP`: Stale, missing, or malformed fixture/test data.
  - `API_CONTRACT`: Schema divergence or payload structure mismatch.
  - `ASSERTION`: Overly strict or erroneous test assertions.
  - `ENVIRONMENT`: Downtime, DNS, network timeouts, or 5xx gateway errors.
  - `TIMING`: Race conditions and eventual consistency delays.
  - `OTHER`: Unclassified failures.
- Produces a concise **summary**, identifies the **single most likely cause**, and gives **3 actionable remediation checks**.

#### 2. Advisory Response Verification (`ApiResponseVerifier`)
- Located in [ApiResponseVerifier](file:///src/test/java/com/api/ai/verify/ApiResponseVerifier.java).
- Allows step definitions to query the AI layer for semantic advisory insights when field-level mismatches occur without altering the deterministic pass/fail result of the test.

#### 3. Run Summary Generation (`RunSummaryGenerator`)
- [RunSummaryGenerator](file:///src/test/java/com/api/ai/analysis/RunSummaryGenerator.java) aggregates all failure outcomes across the test suite run.
- Calls OpenAI to synthesize a concise 1–3 sentence executive summary explaining recurring themes (e.g., global auth outage vs. specific payload regressions).

#### 4. Visual Dashboard & Machine-Readable Artifacts
- **Interactive HTML Dashboard** (`target/ai-analysis/ai-dashboard.html`): Authored by [AiDashboardWriter](file:///src/test/java/com/api/ai/report/AiDashboardWriter.java), presenting category badges, failure summaries, root causes, and recommended checks.
- **Structured JSON Report** (`target/ai-analysis/ai-report.json`): Complete serializable JSON schema model for downstream CI/CD pipeline integration.
- **Audit Log** (`target/ai-analysis/ai-audit.jsonl`): Real-time newline-delimited JSON audit trail tracking timestamps, scenarios, and resolution outcomes.

---

### AI Architecture & Components

| Package / Component | Description |
|---|---|
| [`com.api.ai.config.AiConfig`](file:///src/test/java/com/api/ai/config/AiConfig.java) | Central configuration resolver reading from environment variables, JVM system properties, and `test-config.properties`. |
| [`com.api.ai.client.OpenAiClient`](file:///src/test/java/com/api/ai/client/OpenAiClient.java) | Zero-dependency, lightweight HTTP client built on Java 11 `HttpClient` and Jackson to interact with OpenAI Chat Completions. |
| [`com.api.ai.orchestration.AiOrchestrator`](file:///src/test/java/com/api/ai/orchestration/AiOrchestrator.java) | Single entry point controlling prompt construction, security sanitization, API execution, schema validation, and audit recording. |
| [`com.api.ai.security.AiPromptSanitizer`](file:///src/test/java/com/api/ai/security/AiPromptSanitizer.java) | Regular expression redaction engine stripping Bearer tokens, passwords, secrets, and API keys before sending prompts to external APIs. |
| [`com.api.ai.runtime.AiRuntimeBudget`](file:///src/test/java/com/api/ai/runtime/AiRuntimeBudget.java) | Thread-safe `AtomicInteger` cost guardrail limiting the maximum number of AI API calls per test execution. |
| [`com.api.ai.analysis.FailureAnalyzer`](file:///src/test/java/com/api/ai/analysis/FailureAnalyzer.java) | Normalizes, validates, and post-processes raw LLM responses into strongly typed `FailureAnalysisResult` objects. |
| [`com.api.ai.report.AiReportEnhancer`](file:///src/test/java/com/api/ai/report/AiReportEnhancer.java) | TestNG listener registered in `suite.xml` that captures failure events and triggers post-suite dashboard generation. |
| `src/test/resources/ai/` | Externalized prompt templates (`prompts/`) and JSON schemas (`schemas/`) defining the contract for AI responses. |

---

### Safety, Privacy & Guardrails

The AI layer adheres to four strict enterprise safety guardrails:

1. **Fail-Open Strategy (`agr.ai.fail-open = true`)**:
   - AI execution is strictly non-blocking. If the OpenAI API is unreachable, times out, encounters a 429 rate limit, or runs out of budget, the core test suite is never failed or halted.
2. **Zero Secret Leakage (`AiPromptSanitizer`)**:
   - All prompts, stack traces, and error payloads pass through the sanitizer before transmission.
   - Automatically redacts `Authorization: Bearer <token>`, `client_secret=...`, `password=...`, and `sk-...` API keys.
3. **Runaway Cost Prevention (`AiRuntimeBudget`)**:
   - Caps the maximum number of API calls per test run (default: 20).
   - If an infrastructure outage causes hundreds of tests to fail, calls after the budget threshold are skipped gracefully.
4. **Strict Schema Conformance**:
   - AI outputs are forced into structured JSON and validated against schemas defined in `src/test/resources/ai/schemas/` before deserializing into Java models.

---

### Configuration & Execution

#### Configuration Properties

Settings are defined in `src/test/resources/test-config.properties` and can be overridden via system properties or environment variables:

| Property | Environment Variable | Default | Description |
|---|---|---|---|
| `agr.ai.enabled` | `AGR_AI_ENABLED` | `false` | Master toggle for enabling AI failure analysis. |
| `openai.api.key` | `OPENAI_API_KEY` | *(None)* | OpenAI API key. Required when AI is enabled. |
| `agr.ai.model` | *(None)* | `gpt-4o-mini` | OpenAI Chat model used for analysis. |
| `agr.ai.budget.max-calls-per-run` | *(None)* | `20` | Maximum number of AI calls allowed per test execution. |
| `agr.ai.fail-open` | *(None)* | `true` | When true, AI errors do not fail the test suite run. |

> **Note:** AI analysis is only activated when **both** `agr.ai.enabled = true` **and** a valid `OPENAI_API_KEY` is provided. Otherwise, all AI components act as silent no-ops.

#### Running Tests with AI Enabled

**Option 1: Using System Properties**
```bash
mvn clean test -Dsuite=suite -Dagr.ai.enabled=true -Dopenai.api.key=sk-your-openai-key
```

**Option 2: Using Environment Variables**
```bash
# Windows (PowerShell)
$env:OPENAI_API_KEY="sk-your-openai-key"
$env:AGR_AI_ENABLED="true"
mvn clean test -Dsuite=suite

# Linux / macOS
export OPENAI_API_KEY="sk-your-openai-key"
export AGR_AI_ENABLED="true"
mvn clean test -Dsuite=suite
```

---

### AI Output Artifacts

When AI analysis executes, the following reports are generated under `target/ai-analysis/`:

* **`ai-dashboard.html`**: A modern HTML dashboard displaying:
  - Run metadata and synthesized plain-English executive summary.
  - Category distribution badges (`AUTH`, `API_CONTRACT`, `DATA_SETUP`, etc.).
  - Scenario cards detailing the likely cause and specific actionable check items.
* **`ai-report.json`**: An array of `FailureAnalysisResult` objects containing raw and normalized analysis data.
* **`ai-audit.jsonl`**: A persistent log recording timestamped events and invocation outcomes.

---

## Framework Implementation Details

1. **Base Service Layer (`com.api.base`)**:
   - `BaseService.java`: Core wrapper around REST Assured providing standardized HTTP verb methods (GET, POST, PUT, DELETE), request specification builders, and base URL setup.
   - Domain-specific service classes (`AuthService`, `BookingService`, `EventService`, `HealthService`) extend `BaseService` to encapsulate endpoint URIs, parameters, and business logic.

2. **POJO Models (`com.api.models`)**:
   - **Request Models (`request`)**: POJO classes (`LoginRequest`, `RegisterRequest`, `BookingRequest`, `EventRequest`) serialized into JSON via Jackson.
   - **Response Models (`response`)**: POJO classes (`LoginResponse`, `BookingResponse`, `EventResponse`) deserialized for type-safe assertions and context chaining.

3. **BDD Cucumber Step Definitions & Context (`com.api.steps`, `com.api.context`)**:
   - `ScenarioContext`: Thread-safe state storage (`ThreadLocal`) sharing dynamic values (auth tokens, booking IDs, response instances) between consecutive Cucumber steps.
   - Step definition classes (`AuthSteps`, `BookingSteps`, `EventSteps`, `CommonSteps`) map Gherkin feature steps to Java service interactions.

4. **Lifecycle Hooks & Listeners (`com.api.hooks`, `com.api.listeners`)**:
   - `CucumberHooks`: Manages setup and teardown routines per scenario.
   - `TestListener`: TestNG `ITestListener` generating ExtentReports.
   - `AiReportEnhancer`: TestNG listener orchestrating AI failure analysis and dashboard generation.

5. **Filters & Logging (`com.api.filters`)**:
   - `LoggingFilters.java`: Intercepts REST Assured requests and responses, routing structured logs to console and log files via Log4j2.

---

## Running Tests

### Standard Test Execution (AI Disabled)
```bash
mvn clean test -Dsuite=suite
```

### Running Specific Cucumber Tags
```bash
mvn test -Dsuite=suite -Dcucumber.filter.tags="@auth"
mvn test -Dsuite=suite -Dcucumber.filter.tags="@booking"
```

---

## Reporting

- **AI Analysis Dashboard:** Open `target/ai-analysis/ai-dashboard.html` in your browser.
- **Cucumber HTML Report:** Open `target/cucumber-reports/report.html`.
- **Allure Report:**
  ```bash
  allure serve target/allure-results
  ```
- **Extent Report:** Generated in the test output directory configured via `TestListener.java`.
