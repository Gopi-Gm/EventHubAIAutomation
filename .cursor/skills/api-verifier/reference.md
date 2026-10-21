# AGR API Verifier — Reference

## REST Assured Assertion Patterns

### Standard status code + body assertion pattern
```java
Response response = petService.getPetById(petId);
Assert.assertEquals(response.statusCode(), 200, "Expected 200 OK");

// Deserialize to POJO
Pet pet = response.as(Pet.class);
Assert.assertEquals(pet.getName(), expectedName, "Pet name mismatch");
```

### Advisory pattern for field mismatches
```java
try {
    Assert.assertEquals(pet.getStatus(), "available", "Status mismatch");
} catch (AssertionError e) {
    // Get AI advisory BEFORE re-throwing — does not change outcome
    ApiResponseVerifier.adviseOnMismatch(
        "status",              // field name
        "available",           // expected
        pet.getStatus(),       // actual
        response.asString()    // raw response for context
    );
    throw e;  // always re-throw to keep test failing
}
```

### When to use advisory vs standard assertion
| Use standard assertion when...         | Use advisory when...                        |
|----------------------------------------|---------------------------------------------|
| Field is critical and must match       | Field is non-critical and behavior changed  |
| Testing API contract compliance        | Investigating why a test suddenly broke     |
| Regression tests on stable fields      | A deploy just happened and tests are failing |

## Key Classes
- `com.api.ai.verify.ApiResponseVerifier` — main advisory entry point
- `com.api.ai.orchestration.AiOrchestrator` — underlying AI engine
- `com.api.ai.analysis.FailureAnalysisResult` — structured advisory output
