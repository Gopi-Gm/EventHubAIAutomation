package com.api.steps;

import com.api.base.AuthService;
import com.api.context.ScenarioContext;
import com.api.utility.AssertionUtils;
import com.api.utility.ConfigReader;
import com.api.utility.DataUtils;
import com.api.utility.JsonUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Step definitions for the Authentication feature (auth.feature).
 *
 * PicoContainer creates one ScenarioContext instance per scenario and
 * injects it here AND into every other step-def class that declares it
 * in its constructor — enabling safe, cross-class state sharing.
 */
public class AuthSteps {

    private static final String VALID_EMAIL    = ConfigReader.getEmail();
    private static final String VALID_PASSWORD = ConfigReader.getPassword();

    private final ScenarioContext context;
    private final AuthService authService;

    // Scenario-scoped flag (safe because PicoContainer gives one instance per scenario)
    private boolean useValidCredentials = true;

    public AuthSteps(ScenarioContext context) {
        this.context = context;
        this.authService = new AuthService();
    }

    // ------------------------------------------------------------------ //
    //  Given — credential / pre-condition setup
    // ------------------------------------------------------------------ //

    @Given("I have valid login credentials")
    public void iHaveValidLoginCredentials() {
        useValidCredentials = true;
    }

    @Given("I have invalid login credentials")
    public void iHaveInvalidLoginCredentials() {
        useValidCredentials = false;
    }

    @Given("I login with email {string} and password {string}")
    public void iLoginWithEmailAndPassword(String email, String password) {
        Response response = authService.login(email, password);
        context.setLastResponse(response);
    }

    /**
     * Shared Background step used in Events and Bookings feature files.
     * Performs login, validates success, extracts and stores the JWT token.
     */
    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAsWithPassword(String email, String password) {
        Response response = authService.login(email, password);
        AssertionUtils.assertStatusCode(response, 200);

        String token = JsonUtils.extractToken(response);
        Assert.assertNotNull(token,
                "JWT token not found in login response. Body: " + response.getBody().asString());
        context.setJwtToken(token);
    }

    // ------------------------------------------------------------------ //
    //  When — action steps
    // ------------------------------------------------------------------ //

    @When("I send a login request")
    public void iSendALoginRequest() {
        Response response = useValidCredentials
                ? authService.login(VALID_EMAIL, VALID_PASSWORD)
                : authService.login("notexist@example.com", "WrongPass999");
        context.setLastResponse(response);
    }

    @When("I request the current user profile")
    public void iRequestCurrentUserProfile() {
        if (context.getJwtToken() != null) {
            authService.setAuthToken(context.getJwtToken());
        }
        Response response = authService.getMe();
        context.setLastResponse(response);
    }

    @When("I register a new user with first name {string} last name {string} email {string} password {string} and confirm {string}")
    public void iRegisterNewUser(String firstName, String lastName,
                                  String email, String password, String confirmPassword) {
        // Use a dynamic email if the literal is "dynamic" to avoid duplicate email errors
        if (email.contains("dynamic")) {
            email = DataUtils.generateDynamicEmail("testuser");
        }
        Response response = authService.register(firstName, lastName, email, password, confirmPassword);
        context.setLastResponse(response);
    }
}
