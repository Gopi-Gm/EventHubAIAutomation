---
name: pojogenerator
description: >-
  Use this skill when the user invokes /pojogenerator or asks to generate
  Java POJO model classes (request and/or response) from a JSON payload,
  JSON schema, Swagger/OpenAPI snippet, or plain field description for the
  EventHubAIAutomation project.
  Produces *Request.java in src/test/java/com/api/models/request/ and/or
  *Response.java in src/test/java/com/api/models/response/ following all
  project conventions (Jackson, @JsonIgnoreProperties, @JsonProperty, etc.).
---

# POJO Generator Skill — EventHubAIAutomation

Generates Java POJO model classes — **Request** and/or **Response** — from a
user-supplied JSON payload, JSON schema, OpenAPI/Swagger snippet, or plain
field list, following every convention already established in this project.

---

## Project POJO Conventions (MANDATORY — Study Before Generating)

### Package Locations

| POJO type     | Package                  | Directory                                       |
|---------------|--------------------------|-------------------------------------------------|
| Request model | `com.api.models.request` | `src/test/java/com/api/models/request/`         |
| Response model| `com.api.models.response`| `src/test/java/com/api/models/response/`        |

### Naming Convention

| Input domain | Request class    | Response class    |
|--------------|------------------|-------------------|
| `payment`    | `PaymentRequest` | `PaymentResponse` |
| `event`      | `EventRequest`   | `EventResponse`   |
| `booking`    | `BookingRequest` | `BookingResponse` |

> Rule: PascalCase domain name + `Request` or `Response` suffix.

### Existing POJOs (DO NOT regenerate — check before writing)

**Request models already present:**
- `LoginRequest` — fields: `email`, `password`
- `RegisterRequest` — fields: `firstName`, `lastName`, `email`, `password`, `confirmPassword`
- `BookingRequest` — fields: `eventId`, `customerName`, `customerEmail`, `customerPhone`, `quantity`, `totalPrice`
- `EventRequest` — fields: `title`, `description`, `category`, `eventDate`, `eventTime`,
  `location` (@JsonProperty → `"venue"`), `city`, `capacity` (@JsonProperty → `"totalSeats"`),
  `price`, `image` (@JsonProperty → `"imageUrl"`)

**Response models already present:**
- `LoginResponse` — fields: `token`, `tokenType`, `data`
- `BookingResponse` — fields: `id`, `eventId`, `numberOfTickets`, `totalPrice`, `status`
- `EventResponse` — fields: `id`, `title`, `description`, `category`, `eventDate`, `eventTime`,
  `location`, `city`, `capacity`, `price`, `image`

---

## JSON-to-Java Type Mapping

| JSON type         | Java type  | Notes                                         |
|-------------------|------------|-----------------------------------------------|
| `string`          | `String`   | Always                                        |
| `integer` / `int` | `int`      | Use `Integer` only when field can be null     |
| `number` / `float`| `double`   | Use `Double` only when field can be null      |
| `boolean`         | `boolean`  | Use `Boolean` only when field can be null     |
| `array`           | `List<T>`  | Import `java.util.List` + `java.util.ArrayList`|
| `object`          | Nested POJO| Generate a separate inner or top-level class  |
| unknown / `any`   | `Object`   | Fallback — add comment: `// confirm type`     |

---

## Request POJO Rules

1. **No class-level annotation** — request POJOs have NO `@JsonIgnoreProperties`
2. **`@JsonProperty` only when JSON field name ≠ Java camelCase name** — annotate on BOTH the field AND the getter
3. **Two constructors** — no-arg constructor + all-args constructor
4. **One-liner getters/setters** — `public String getFoo() { return foo; }` on the same line
5. **Field order** — match the order fields appear in the user-supplied JSON/schema
6. **Import** `com.fasterxml.jackson.annotation.JsonProperty` only when `@JsonProperty` is actually used
7. **No Lombok** — manual getters/setters only, no annotations like `@Data` or `@Builder`

### Request POJO Template

```java
package com.api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty; // include ONLY if @JsonProperty is used

/**
 * Request model for {Domain} operations.
 *
 * @JsonProperty annotations (only when Java name differs from JSON name):
 *   Java field    ->  JSON field sent to API
 *   ----------        ----------------------
 *   {javaField}   ->  {jsonField}
 */
public class {Domain}Request {

    // Fields whose Java name matches JSON name — no annotation
    private {Type} {fieldName};

    // Fields whose Java name differs from JSON name — annotate field
    @JsonProperty("{jsonName}")
    private {Type} {javaField};

    public {Domain}Request() {
    }

    public {Domain}Request({Type} {field1}, {Type} {field2} /*, ...*/) {
        this.{field1} = {field1};
        this.{field2} = {field2};
    }

    // Plain getter/setter (name matches)
    public {Type} get{Field}()               { return {fieldName}; }
    public void   set{Field}({Type} {field}) { this.{fieldName} = {field}; }

    // Getter/setter with @JsonProperty (name mismatch)
    @JsonProperty("{jsonName}")
    public {Type} get{Field}()               { return {javaField}; }
    public void   set{Field}({Type} {field}) { this.{javaField} = {field}; }
}
```

---

## Response POJO Rules

1. **`@JsonIgnoreProperties(ignoreUnknown = true)`** — ALWAYS at class level
2. **Import `com.fasterxml.jackson.annotation.JsonIgnoreProperties`** — always
3. **Import `com.fasterxml.jackson.annotation.JsonProperty`** — only when `@JsonProperty` is used
4. **Always include `String id` as the FIRST field** — every response object from this API returns an `id`
5. **No all-args constructor** — only the no-arg constructor
6. **One-liner getters/setters** — same style as request POJOs
7. **`List<T>` fields** — import `java.util.List` and `java.util.ArrayList`, initialise field inline: `= new ArrayList<>()`

### Response POJO Template

```java
package com.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // include ONLY if @JsonProperty is used
import java.util.ArrayList;                           // include ONLY if List field present
import java.util.List;                                // include ONLY if List field present

@JsonIgnoreProperties(ignoreUnknown = true)
public class {Domain}Response {

    private String id;          // always first

    private {Type} {fieldName};

    @JsonProperty("{jsonName}") // only when JSON name != Java name
    private {Type} {javaField};

    public {Domain}Response() {
    }

    public String getId()            { return id; }
    public void   setId(String id)   { this.id = id; }

    public {Type} get{Field}()               { return {fieldName}; }
    public void   set{Field}({Type} {field}) { this.{fieldName} = {field}; }

    @JsonProperty("{jsonName}")
    public {Type} get{Field}()               { return {javaField}; }
    public void   set{Field}({Type} {field}) { this.{javaField} = {field}; }
}
```

---

## Step-by-Step Generation Process

### Step 1 — Parse the Input

Accept any of these input formats:
- **Raw JSON** payload (request or response body)
- **JSON Schema** snippet
- **OpenAPI / Swagger** `requestBody` or `responses` block
- **Plain field list**: `fieldName: type` pairs
- **Both** request + response JSON together → generate both POJOs

Extract:
- **Domain name**: derive from endpoint path or user instruction (e.g. `/payments` → `Payment`)
- **Fields**: name, JSON type, and whether JSON name differs from Java camelCase name
- **Nullable**: use boxed type when schema marks `nullable: true`

### Step 2 — Check What Already Exists

Before generating, verify:
- Does `{Domain}Request.java` already exist in `src/test/java/com/api/models/request/`?
- Does `{Domain}Response.java` already exist in `src/test/java/com/api/models/response/`?
- **Only generate what is missing.** If a POJO already exists, warn the user — do NOT overwrite silently.

### Step 3 — Determine What to Generate

| User says                       | Generate                          |
|---------------------------------|-----------------------------------|
| "request only"                  | `{Domain}Request.java` only       |
| "response only"                 | `{Domain}Response.java` only      |
| "both" or no qualifier          | Both Request + Response           |
| JSON contains `"id"` field      | Treat as Response POJO            |
| JSON contains no `"id"` field   | Treat as Request POJO             |

### Step 4 — Map Fields

For each JSON field:
1. **Sanitize Java name**: convert `snake_case` / `kebab-case` → `camelCase`
   - `event_id` → `eventId`, `created-at` → `createdAt`, `totalSeats` stays `totalSeats`
2. **Detect name mismatch**: if sanitized Java name ≠ original JSON key → add `@JsonProperty("originalKey")`
3. **Map type** using the JSON-to-Java table above
4. **Nullable**: if schema marks `nullable: true` or field may be absent → use boxed type

### Step 5 — Generate the POJO File(s)

Follow the templates above exactly. Final checklist before writing:
- [ ] Correct package declaration
- [ ] Only needed imports (no unused imports)
- [ ] `@JsonIgnoreProperties(ignoreUnknown = true)` on response class, absent on request class
- [ ] `@JsonProperty` on **field AND getter** when name differs
- [ ] No-arg constructor always present
- [ ] All-args constructor in request POJO, absent in response POJO
- [ ] One-liner getters/setters
- [ ] `String id` is the FIRST field in response POJOs
- [ ] `List<T>` fields initialised to `new ArrayList<>()`

### Step 6 — Write the File(s)

- Write `{Domain}Request.java`  → `src/test/java/com/api/models/request/{Domain}Request.java`
- Write `{Domain}Response.java` → `src/test/java/com/api/models/response/{Domain}Response.java`

### Step 7 — Confirm to the User

Report exactly in this format:
```
✅ Generated POJOs:
   ☕ Request  : src/test/java/com/api/models/request/{Domain}Request.java   ({N} fields)
   ☕ Response : src/test/java/com/api/models/response/{Domain}Response.java ({N} fields)

📌 Notes:
   - @JsonProperty applied to : {comma-separated list of remapped fields, or "none"}
   - Nullable / boxed fields  : {list, or "none"}
   - Next step: use these POJOs in a new *Service.java, or run /featuregenerator to generate full BDD tests.
```

---

## Worked Examples

### Example 1 — From raw JSON request payload

**User input:**
```
/pojogenerator domain=Payment request:
{ "event_id": "abc123", "amount": 299.99, "payment_method": "CARD", "coupon_code": "SAVE10" }
```

**Generated** `src/test/java/com/api/models/request/PaymentRequest.java`:
```java
package com.api.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for Payment operations.
 *
 * @JsonProperty annotations:
 *   Java field    ->  JSON field sent to API
 *   ----------        ----------------------
 *   eventId       ->  event_id
 *   paymentMethod ->  payment_method
 *   couponCode    ->  coupon_code
 */
public class PaymentRequest {

    @JsonProperty("event_id")
    private String eventId;

    private double amount;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("coupon_code")
    private String couponCode;

    public PaymentRequest() {
    }

    public PaymentRequest(String eventId, double amount, String paymentMethod, String couponCode) {
        this.eventId       = eventId;
        this.amount        = amount;
        this.paymentMethod = paymentMethod;
        this.couponCode    = couponCode;
    }

    @JsonProperty("event_id")
    public String getEventId()               { return eventId; }
    public void   setEventId(String eventId) { this.eventId = eventId; }

    public double getAmount()                { return amount; }
    public void   setAmount(double amount)   { this.amount = amount; }

    @JsonProperty("payment_method")
    public String getPaymentMethod()                     { return paymentMethod; }
    public void   setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @JsonProperty("coupon_code")
    public String getCouponCode()                  { return couponCode; }
    public void   setCouponCode(String couponCode) { this.couponCode = couponCode; }
}
```

---

### Example 2 — From raw JSON response payload

**User input:**
```
/pojogenerator domain=Payment response:
{ "id": "pay_001", "event_id": "abc123", "amount": 299.99, "status": "SUCCESS", "created_at": "2026-09-01T10:00:00Z" }
```

**Generated** `src/test/java/com/api/models/response/PaymentResponse.java`:
```java
package com.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private String id;

    @JsonProperty("event_id")
    private String eventId;

    private double amount;
    private String status;

    @JsonProperty("created_at")
    private String createdAt;

    public PaymentResponse() {
    }

    public String getId()            { return id; }
    public void   setId(String id)   { this.id = id; }

    @JsonProperty("event_id")
    public String getEventId()               { return eventId; }
    public void   setEventId(String eventId) { this.eventId = eventId; }

    public double getAmount()                { return amount; }
    public void   setAmount(double amount)   { this.amount = amount; }

    public String getStatus()                { return status; }
    public void   setStatus(String status)   { this.status = status; }

    @JsonProperty("created_at")
    public String getCreatedAt()                 { return createdAt; }
    public void   setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
```

---

### Example 3 — From plain field list (generate both)

**User input:**
```
/pojogenerator domain=Review both
Request fields : eventId(string), rating(int), comment(string)
Response fields: id(string), eventId(string), userId(string), rating(int), comment(string), createdAt(string)
```

- `ReviewRequest.java` — 3 fields, no `@JsonProperty` needed (all names are already camelCase)
- `ReviewResponse.java` — 6 fields with `id` first, `@JsonIgnoreProperties(ignoreUnknown = true)`

---

## Edge Cases to Handle

| Situation                                   | Action                                                                                          |
|---------------------------------------------|-------------------------------------------------------------------------------------------------|
| Field name is a Java keyword (`class`, `default`) | Rename (e.g. `defaultValue`), add `@JsonProperty` with original name                    |
| Array field (`"tags": ["a","b"]`)           | `private List<String> tags = new ArrayList<>();` — import `List` + `ArrayList`                  |
| Nested object (`"address": {…}`)            | Generate a separate POJO class; reference it as the field type                                  |
| Field is `null` in sample JSON              | Use `String`, add comment: `// nullable — confirm type with API team`                           |
| User provides only request or response      | Generate only what was requested; note the other was skipped                                    |
| POJO already exists                         | Warn the user — do NOT overwrite silently; offer to add new fields only                         |
| snake_case JSON key, no semantic change     | Convert to camelCase → add `@JsonProperty` with original snake_case key                         |
| `id` field missing from response JSON       | Still add `private String id;` as first field with getter/setter — all API responses include it |
