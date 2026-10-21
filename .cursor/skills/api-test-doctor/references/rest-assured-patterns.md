# REST Assured Patterns

## BaseService Pattern

All service classes extend `BaseService` which provides:
- `BASE_URL` constant pointing to `https://petstore.swagger.io/v2`
- `requestspecification` with logging filter applied globally
- Helper methods: `postRequest`, `putRequest`, `getRequest`, `deleteRequest`

```java
// Service class example
public class PetService extends BaseService {

    public Response createPet(Pet payload) {
        return postRequest(payload, "/pet");
    }

    public Response getPetById(long petId) {
        return getRequest("/pet/" + petId);
    }

    public Response updatePet(Pet payload) {
        return putRequest(payload, "/pet");
    }

    public Response deletePet(long petId) {
        return deleteRequest("/pet/" + petId);
    }

    public Response getPetsByStatus(String status) {
        return getRequest("/pet/findByStatus?status=" + status);
    }
}
```

## Request Specification Patterns

### With auth token (when needed)
```java
setAuthToken(token);  // from BaseService
Response response = postRequest(payload, "/pet");
```

### With query parameters (direct REST Assured)
```java
// In a service method
Response response = given()
    .baseUri(BASE_URL)
    .queryParam("status", "available")
    .get("/pet/findByStatus");
```

## Response Handling Patterns

### POJO deserialization
```java
Response response = petService.createPet(payload);
Assert.assertEquals(response.statusCode(), 200, "Create pet status");
Pet created = response.as(Pet.class);
```

### JsonPath extraction (for simple cases)
```java
long petId = response.jsonPath().getLong("id");
String status = response.jsonPath().getString("status");
List<String> tags = response.jsonPath().getList("tags.name");
```

### List responses
```java
Response response = petService.getPetsByStatus("available");
List<Pet> pets = Arrays.asList(response.as(Pet[].class));
Assert.assertFalse(pets.isEmpty(), "Should return at least one available pet");
```

## Logging Filter
`LoggingFilters` is registered globally in `BaseService` static block.
All requests and responses are logged automatically — no need to add `.log().all()` manually.

## Common Mistakes
| Mistake | Fix |
|---|---|
| `given().get(BASE_URL + "/pet/1")` | Use service layer instead |
| `.log().all()` on every request | Already handled by `LoggingFilters` |
| `response.getBody().asString()` for assertions | Use `.as(Pet.class)` or `.jsonPath()` |
| Hardcoding `"https://petstore.swagger.io/v2"` in tests | Use `BaseService.BASE_URL` constant |
