---
name: featuregenerator
description: >-
  Use this skill when the user invokes /featuregenerator or asks to generate
  a Cucumber BDD feature file and matching Java step definitions from a JSON
  payload or API description for the EventHubAIAutomation project.
  Produces a .feature file in src/test/resources/features/ and a *Steps.java
  file in src/test/java/com/api/steps/ following all project conventions.
---

# Feature Generator Skill — EventHubAIAutomation

Generates a Cucumber `.feature` file **and** matching `*Steps.java` step definitions
from a user-supplied JSON payload or plain API description.

---

## Project Reference (Read Before Generating)

| Layer | Location | Purpose |
|-------|----------|---------|
| Feature files | `src/test/resources/features/*.feature` | Gherkin scenarios |
| Step defs | `src/test/java/com/api/steps/*Steps.java` | Gherkin → Java bridge |
| Services | `src/test/java/com/api/base/*Service.java` | REST Assured HTTP calls |
| Models (request) | `src/test/java/com/api/models/request/*.java` | Request POJOs |
| Models (response) | `src/test/java/com/api/models/response/*.java` | Response POJOs |
| Context | `src/test/java/com/api/context/ScenarioContext.java` | Cross-step state |
| Base service | `src/test/java/com/api/base/BaseService.java` | `postRequest()`, `getRequest()`, `putRequest()`, `deleteRequest()`, `setAuthToken()` |

---

## Existing Reusable Steps (DO NOT re-implement these)

### CommonSteps.java
- `Then the response status code should be {int}`
- `Then the response body should contain {string}`
- `And the response body should not be empty`

### AuthSteps.java
- `Given I am logged in as {string} with password {string}` ← Background login
- `Given I have valid login credentials`
- `Given I have invalid login credentials`
- `Given I login with email {string} and password {string}`
- `When I send a login request`
- `When I request the current user profile`

### EventSteps.java
- `When I request all events`
- `When I create an event with title {string} in city {string}`
- `And the event is created successfully`
- `When I update the event title to {string}`
- `When I delete the created event`
- `When I fetch the created event by ID`
- `When I fetch event by ID {string}`
- `When I search events with query {string}`
- `When I get events filtered by category {string}`
- `When I get events filtered by city {string}`
- `When I request all categories`
- `When I request all cities`

### BookingSteps.java
- `When I request all bookings`
- `Given an event exists to book`
- `When I create a booking for {int} tickets with payment method {string}`
- `And the booking is created successfully`
- `When I update the booking to {int} tickets`
- `When I cancel the booking`
- `When I fetch the created booking by ID`
- `When I fetch booking by ID {string}`

---

## Generation Rules (MANDATORY)

1. **Status code assertion BEFORE body assertion** — always
2. **No hardcoded test data in step text** — use `{string}` / `{int}` parameters
3. **Background login** — include `Background:` block with `Given I am logged in as "prem@yopmail.com" with password "Automation@2026"` for any authenticated feature
4. **Scenario independence** — each scenario must work alone (PicoContainer creates fresh context per scenario)
5. **Reuse existing steps** — never duplicate a step already defined above
6. **Descriptive scenario names** — action-oriented, e.g. `Create booking with valid payment method`
7. **Section comments** — wrap each scenario with `# ----` comment block and number it
8. **Step class naming** — `{Domain}Steps.java` e.g. `PaymentSteps.java`
9. **Constructor injection** — `public {Domain}Steps(ScenarioContext context)` with PicoContainer
10. **applyToken() helper** — always include and call before authenticated HTTP calls

> **⚠️ ScenarioContext pattern**: `ScenarioContext` has typed fields only (`jwtToken`, `createdEventId`, `createdBookingId`, `lastResponse`).
> For domain-specific IDs (e.g. `createdPaymentId`), declare a **private instance field** on the `*Steps.java` class itself — it is safe because PicoContainer creates one Steps instance per scenario.

---

## Step-by-Step Generation Process

### Step 1 — Parse the Input

Read the user's JSON or API description. Extract:
- **Endpoint(s)**: path and HTTP method (GET/POST/PUT/DELETE)
- **Request fields**: field names and types (for the request POJO)
- **Response fields**: field names expected in the response body
- **Domain name**: derive from endpoint (e.g., `/payments` → `Payment`)

### Step 2 — Identify What Already Exists

Check if:
- A `*Service.java` already exists for this domain in `src/test/java/com/api/base/`
- A request model POJO already exists in `src/test/java/com/api/models/request/`
- Any steps are already defined in an existing `*Steps.java`

**Only generate what is missing.**

### Step 3 — Design the Scenarios

For each endpoint generate at minimum:
| Endpoint | Scenarios to Generate |
|----------|-----------------------|
| `GET /resource` | Happy path (200), Unauthorized (401) |
| `GET /resource/{id}` | Valid ID (200), Invalid ID (404) |
| `POST /resource` | Valid payload (200/201), Missing required field (400) |
| `PUT /resource/{id}` | Valid update (200), Invalid ID (404) |
| `DELETE /resource/{id}` | Valid delete (200), Already deleted (404) |

### Step 4 — Generate the Feature File

File path: `src/test/resources/features/{domain_lowercase}.feature`

Template:
```gherkin
Feature: EventHub {Domain} Management

  Background:
    Given I am logged in as "prem@yopmail.com" with password "Automation@2026"

  # ------------------------------------------------------------------ #
  # Scenario 1: {Descriptive name}
  # ------------------------------------------------------------------ #
  Scenario: {Descriptive scenario name}
    When  {action step}
    Then  the response status code should be {code}
    And   {optional body assertion}
```

### Step 5 — Generate the Step Definitions Java File

File path: `src/test/java/com/api/steps/{Domain}Steps.java`

Template:
```java
package com.api.steps;

import com.api.context.ScenarioContext;
import com.api.base.{Domain}Service;
// import models as needed
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.restassured.response.Response;
import org.testng.Assert;

public class {Domain}Steps {

    private final ScenarioContext context;
    private final {Domain}Service {domain}Service;

    public {Domain}Steps(ScenarioContext context) {
        this.context = context;
        this.{domain}Service = new {Domain}Service();
    }

    private void applyToken() {
        String token = context.getJwtToken();
        if (token != null) {
            {domain}Service.setAuthToken(token);
        }
    }

    @When("{gherkin step pattern}")
    public void {methodName}({params}) {
        applyToken();
        Response response = {domain}Service.{serviceMethod}({args});
        context.setLastResponse(response);
    }
}
```

### Step 6 — Write Both Files

- Write the `.feature` file to `src/test/resources/features/`
- Write the `*Steps.java` file to `src/test/java/com/api/steps/`
- If a new `*Service.java` is needed, write it to `src/test/java/com/api/base/`
- If a new request POJO is needed, write it to `src/test/java/com/api/models/request/`

### Step 7 — Confirm to the User

After writing both files, report:
```
✅ Generated:
   📄 Feature file : src/test/resources/features/{name}.feature  ({N} scenarios)
   ☕ Step defs    : src/test/java/com/api/steps/{Name}Steps.java ({N} new steps)
   [optional] ☕ Service     : src/test/java/com/api/base/{Name}Service.java
   [optional] ☕ Model       : src/test/java/com/api/models/request/{Name}Request.java

▶ Run with: mvn test -Dsuite=suite
```

---

## Example Invocation

User says:
> `/featuregenerator` use this JSON: `{"endpoint": "POST /payments", "fields": {"eventId": "string", "amount": "number", "method": "string"}}`

Agent output:
- `src/test/resources/features/payments.feature` with scenarios: happy path, missing eventId, invalid amount, unauthorized
- `src/test/java/com/api/steps/PaymentSteps.java` with all step bindings
- `src/test/java/com/api/base/PaymentService.java` (new service)
- `src/test/java/com/api/models/request/PaymentRequest.java` (new POJO)
