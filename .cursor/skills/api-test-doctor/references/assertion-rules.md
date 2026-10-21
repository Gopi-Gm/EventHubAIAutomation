# Assertion Rules

## TestNG Assertion Standards

### Always use 3-argument form
```java
// ✅ Correct — always include a message
Assert.assertEquals(actual, expected, "Descriptive message about what was checked");

// ❌ Wrong — no message makes failures hard to diagnose
Assert.assertEquals(actual, expected);
```

### Order of arguments
TestNG uses `(actual, expected, message)`:
```java
Assert.assertEquals(response.statusCode(), 200, "Expected 200 OK for pet creation");
Assert.assertEquals(pet.getName(), "Buddy", "Pet name should match input");
Assert.assertTrue(pet.getId() > 0, "Pet ID should be a positive integer after creation");
Assert.assertNotNull(pet.getStatus(), "Pet status should not be null");
```

## Assertion Ordering in Tests
1. Status code assertion **always first**
2. Non-null / existence checks
3. Specific field value assertions
4. List/collection size assertions (if applicable)

## Common Assertion Anti-patterns

| Anti-pattern | Problem | Fix |
|---|---|---|
| `Assert.assertEquals(response.asString(), "{...}")` | Fragile — any field order change breaks it | Assert individual fields |
| `Assert.assertEquals(list.size(), 5)` | Depends on external data state | Use `Assert.assertTrue(list.size() > 0)` |
| Hard-coded IDs in assertions | Brittle — data changes break tests | Use variables from setup or response |
| Asserting optional fields for exact null | May vary by environment | Use `assertNotNull` or skip optional |

## Handling Nullable Fields
```java
// For optional fields — check presence before asserting value
if (pet.getCategory() != null) {
    Assert.assertEquals(pet.getCategory().getName(), expectedCategory,
            "Category name mismatch");
}
```

## Soft Assertions (when needed)
If multiple field assertions should ALL be reported even on first failure:
```java
// TestNG soft assertions
SoftAssert softAssert = new SoftAssert();
softAssert.assertEquals(pet.getName(), "Buddy", "Name mismatch");
softAssert.assertEquals(pet.getStatus(), "available", "Status mismatch");
softAssert.assertAll(); // reports all failures at once
```
