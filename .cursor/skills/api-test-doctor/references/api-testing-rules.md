# API Testing Rules

## Core Principles

1. **Always assert the status code first**, before any body field assertions
2. **Assert specific fields** — never assert the entire response body as a string
3. **Use meaningful assertion messages** — always pass a third argument to `Assert.*`
4. **Test one behavior per test method** — don't combine multiple business scenarios in one test
5. **Don't rely on test execution order** — each test must set up its own data

## Status Code Rules

| Scenario | Expected Code |
|----------|--------------|
| GET (resource exists) | 200 |
| POST (create) | 200 (Petstore API) or 201 |
| PUT (update, exists) | 200 |
| DELETE (success) | 200 |
| GET (not found) | 404 |
| POST (bad input) | 400 or 405 |

## REST Assured Usage

### Preferred pattern (via service layer)
```java
// Good — uses service layer abstraction
Response response = petService.getPetById(petId);
```

### Direct REST Assured (use only in BaseService or service classes)
```java
// Acceptable in service classes
Response response = given()
    .baseUri(BASE_URL)
    .contentType(ContentType.JSON)
    .body(payload)
    .post("/pet");
```

## Response Deserialization
```java
// Preferred: deserialize to typed POJO
Pet pet = response.as(Pet.class);
Assert.assertEquals(pet.getName(), expectedName, "Pet name mismatch");

// Acceptable: JsonPath for quick field extraction
String name = response.jsonPath().getString("name");
```

## Content Type
- Always set `ContentType.JSON` on POST and PUT requests
- GET and DELETE requests do not need content type header
