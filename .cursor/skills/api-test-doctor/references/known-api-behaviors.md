# Known API Behaviors — PetStore API

## Base URL
`https://petstore.swagger.io/v2`

## Known Behaviors and Quirks

### 1. ID generation
- The API auto-generates pet IDs as `long` integers
- You can optionally provide an ID in the create payload, but this is not recommended
- Always read the ID from the create response, not from the request payload

### 2. Status field values
- Valid statuses: `available`, `pending`, `sold`
- Case-sensitive — `Available` will NOT match `available` in responses
- Invalid statuses return 400 in strict implementations, but Petstore may accept them

### 3. Concurrent modifications
- The public Petstore is a **shared sandbox** — other users can modify or delete your data
- If a test suddenly fails with 404 after previously passing, another user likely deleted the resource
- Solution: Always create fresh test data in `@BeforeMethod`

### 4. GET /pet/findByStatus response
- Returns an array (possibly empty)
- If no pets have the requested status, returns `[]` (not 404)
- Response is an array — deserialize as `Pet[]`, not `Pet`

### 5. DELETE response
- Returns `200` with a `ApiResponse` body: `{ "code": 200, "type": "unknown", "message": "<id>" }`
- Does NOT return the deleted pet object

### 6. POST /pet response
- Returns the created pet with the auto-generated `id`
- Use `response.jsonPath().getLong("id")` to capture the ID for subsequent tests

### 7. PUT /pet response
- Returns the updated pet object
- If the pet doesn't exist, Petstore may return 404 or create a new one (behavior varies)

### 8. Rate limiting
- The public Petstore API has no formal rate limits, but excessive requests may cause throttling
- Keep `agr.ai.budget.max-calls-per-run=20` (default) to avoid API key rate limits on OpenAI

## Endpoint Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/pet` | POST | Create a new pet |
| `/pet` | PUT | Update an existing pet |
| `/pet/{petId}` | GET | Get pet by ID |
| `/pet/{petId}` | DELETE | Delete a pet |
| `/pet/findByStatus` | GET | Find pets by status |
| `/store/inventory` | GET | Get inventory counts by status |
| `/user` | POST | Create user |
