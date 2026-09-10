# Demo API Automation Framework

This is a robust and scalable API Automation Framework built using Java, REST Assured, and TestNG. 

## Technology Stack

*   **Language:** Java 11
*   **API Automation:** REST Assured (v5.3.0)
*   **Test Runner & Assertions:** TestNG (v7.10.2)
*   **Data Binding/Serialization:** Jackson (v2.18.2)
*   **Reporting:** ExtentReports (v5.1.1)
*   **Logging:** Log4j2 (v2.20.0)
*   **Build Tool:** Maven

## Project Structure

```text
src
├── main
│   ├── java
│   └── resources
└── test
    ├── java
    │   └── com
    │       └── api
    │           ├── base
    │           │   ├── AuthService.java
    │           │   ├── BaseService.java
    │           │   └── UserProfileManagementService.java
    │           ├── filters
    │           │   └── LoggingFilters.java
    │           ├── listeners
    │           │   └── TestListener.java
    │           ├── models
    │           │   ├── request
    │           │   │   ├── LoginRequest.java
    │           │   │   ├── ProfileUpdateRequest.java
    │           │   │   └── SignUpRequest.java
    │           │   └── response
    │           │       ├── LoginResponse.java
    │           │       └── UserProfileResponse.java
    │           └── tests
    │               ├── AccountCreationTest.java
    │               ├── ForgotPasswordTerst.java
    │               ├── GetProfileRequestTest.java
    │               ├── LoginAPITest.java
    │               ├── LoginAPITest2.java
    │               ├── LoginAPITest3.java
    │               └── UpdateProfileInfoTest.java
    └── resources
        └── log4j2.xml
```

## Implementation Details

The framework is designed with a modular architecture, promoting code reusability and maintainability.

1.  **Base Service Layer (`com.api.base`)**:
    *   `BaseService.java`: Acts as the core wrapper around REST Assured, providing common HTTP methods (GET, POST, PUT, DELETE) and configuration.
    *   `AuthService.java` & `UserProfileManagementService.java`: Specific service classes that extend `BaseService` to handle endpoints related to Authentication and User Profile operations respectively. They encapsulate the endpoint paths and specific request logic.

2.  **POJO Models (`com.api.models`)**:
    *   **Request (`request` package)**: Contains POJO (Plain Old Java Object) classes (e.g., `LoginRequest`, `SignUpRequest`) used to construct JSON request bodies. Jackson handles the serialization of these objects into JSON.
    *   **Response (`response` package)**: Contains POJO classes (e.g., `LoginResponse`, `UserProfileResponse`) used to deserialize JSON responses from the API into Java objects for easy assertion and data extraction.

3.  **Tests (`com.api.tests`)**:
    *   Contains TestNG test classes. These classes call the respective service methods and perform assertions on the response status codes, headers, and body (using the deserialized response objects).

4.  **Listeners (`com.api.listeners`)**:
    *   `TestListener.java`: Implements TestNG's `ITestListener`. It hooks into the test execution lifecycle to generate rich HTML reports using ExtentReports. It logs test success, failure, and skipped statuses.

5.  **Filters (`com.api.filters`)**:
    *   `LoggingFilters.java`: Intercepts REST Assured requests and responses to log the details (URI, Headers, Body) to the console and/or log files using Log4j2.

6.  **Configuration & Logging**:
    *   `log4j2.xml` in `src/test/resources` configures the logging levels and output destinations (console, files).
    *   `pom.xml`: Manages project dependencies and build plugins (like `maven-surefire-plugin` for executing TestNG suites).
    *   `suite.xml`: The TestNG suite file used to group and run specific tests or test classes.
