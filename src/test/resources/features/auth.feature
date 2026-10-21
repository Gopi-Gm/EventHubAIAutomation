Feature: EventHub Authentication

  # ------------------------------------------------------------------ #
  # Scenario 1: Successful login returns a JWT token
  # ------------------------------------------------------------------ #
  Scenario: Login with valid credentials
    Given I have valid login credentials
    When I send a login request
    Then the response status code should be 200
    And the response body should contain "token"

  # ------------------------------------------------------------------ #
  # Scenario 2: Invalid credentials return an error
  # ------------------------------------------------------------------ #
  Scenario: Login with invalid credentials
    Given I have invalid login credentials
    When I send a login request
    Then the response status code should be 400

  # ------------------------------------------------------------------ #
  # Scenario 3: Missing email returns a validation error
  # ------------------------------------------------------------------ #
  Scenario: Login with missing email
    Given I login with email "" and password "Automation@2026"
    Then the response status code should be 400

  # ------------------------------------------------------------------ #
  # Scenario 4: Get current user profile after login
  # ------------------------------------------------------------------ #
  Scenario: Get current user profile after login
    Given I am logged in as "prem@yopmail.com" with password "Automation@2026"
    When I request the current user profile
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 5: Accessing profile without a token is rejected
  # ------------------------------------------------------------------ #
  Scenario: Get current user profile without authentication
    When I request the current user profile
    Then the response status code should be 401

  # ------------------------------------------------------------------ #
  # Scenario 6: Register a new user account
  # ------------------------------------------------------------------ #
  Scenario: Register new user with valid details
    When I register a new user with first name "Test" last name "User" email "dynamic@yopmail.com" password "Test@12345" and confirm "Test@12345"
    Then the response status code should be 200
    And the response body should contain "token"
