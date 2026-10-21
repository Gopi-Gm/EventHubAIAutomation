# AGR API Test Generator Skill

## Purpose
Use this skill to generate new TestNG test methods and supporting model classes
for the PetStore API automation framework.

## When to activate
- Adding tests for a new API endpoint
- Extending coverage on an existing endpoint (new scenarios)
- Generating test data models for a new request/response POJO

## Project conventions (always follow these)

### Package structure
```
com.api.tests.*          ← test classes (extend BaseService or use service layer)
com.api.services.*       ← service classes extending BaseService
com.api.models.request.* ← request POJOs
com.api.models.response.*← response POJOs (or use com.api.models.*)
```

### Test method template
```java
@Test(description = "Verify <action> returns <expected outcome>")
public void test_<EntityName>_<action>_<expectedResult>() {
    // Arrange
    <RequestModel> payload = new <RequestModel>();
    payload.set...(...);

    // Act
    Response response = <service>.<method>(payload);

    // Assert — status code first, then body fields
    Assert.assertEquals(response.statusCode(), <expectedCode>,
            "Unexpected status code");
    <ResponseModel> result = response.as(<ResponseModel>.class);
    Assert.assertEquals(result.get...(), expected..., "Field mismatch");
}
```

### Rules
- Method name: `test_<Entity>_<action>_<result>` (camelCase, no spaces)
- Always assert status code before body fields
- Always pass a description message to Assert
- Tests must be independent — no shared state, no ordering dependencies
- Use constants for endpoint paths, not hardcoded strings

## Examples
See [`examples/party-feature-example.feature`](./examples/party-feature-example.feature)
for a complete Gherkin-style example adapted to TestNG.

## AI-powered generation
When `agr.ai.enabled=true`, use the prompt template at:
`src/test/resources/ai/prompts/scenario-generation.txt`
