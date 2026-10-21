# Framework Standards

## Project Overview
- **Project**: PetStoreAIAutomation
- **Language**: Java 11
- **Test framework**: TestNG 7.x
- **HTTP client**: REST Assured 5.x
- **Reporting**: ExtentReports 5.x + Log4j2
- **AI layer**: com.api.ai.* (OpenAI GPT-4o-mini, opt-in)
- **Build**: Maven

## Package Conventions
| Package | Purpose |
|---------|---------|
| `com.api.base` | `BaseService` — REST Assured wrapper with base URI, filters |
| `com.api.services` | Service classes (one per API resource) extending `BaseService` |
| `com.api.models.request` | Request body POJOs |
| `com.api.models.response` | Response body POJOs (or combined in `com.api.models`) |
| `com.api.tests` | TestNG test classes |
| `com.api.listeners` | TestNG listeners (`TestListener`, `AiReportEnhancer`) |
| `com.api.filters` | REST Assured request/response filters (`LoggingFilters`) |
| `com.api.ai.*` | AI failure analysis layer (opt-in, advisory only) |

## Naming Conventions
- Test methods: `test_<Entity>_<action>_<expectedOutcome>`
- Service methods: `create<Entity>`, `get<Entity>ById`, `update<Entity>`, `delete<Entity>`
- POJO classes: `<Entity>Request`, `<Entity>Response` (or just `<Entity>` if dual-purpose)
- Constants: `UPPER_SNAKE_CASE`, declared `static final`

## Test Independence Rule
Every test method must be completely independent:
- No `@Test(dependsOnMethods = ...)`
- No shared mutable state between tests
- Setup via `@BeforeMethod` where needed
- Teardown via `@AfterMethod` where needed

## Base URL
`https://petstore.swagger.io/v2` — defined in `BaseService.BASE_URL`
