Feature: EventHub Event Management API

  # Example feature showing test scenarios for the EventHub /events endpoint
  # Adapted from the Pet Store API example to EventHub conventions

  Background:
    Given I am logged in as "prem@yopmail.com" with password "Automation@2026"

  # ------------------------------------------------------------------ #
  # Scenario 1: Add a new event to the hub                             #
  # ------------------------------------------------------------------ #
  Scenario: Add a new event to the hub
    When I create an event with title "Tech Summit 2026" in city "San Francisco"
    Then the response status code should be 201
    And the event is created successfully

  # ------------------------------------------------------------------ #
  # Scenario 2: Retrieve an existing event by ID                       #
  # ------------------------------------------------------------------ #
  Scenario: Retrieve an existing event by ID
    When I create an event with title "Lookup Event" in city "New York"
    Then the response status code should be 201
    And the event is created successfully
    When I fetch the created event by ID
    Then the response status code should be 200
    And the response body should contain "Lookup Event"

  # ------------------------------------------------------------------ #
  # Scenario 3: Update an existing event title                         #
  # ------------------------------------------------------------------ #
  Scenario: Update an existing event title
    When I create an event with title "Original Title" in city "Chicago"
    Then the response status code should be 201
    And the event is created successfully
    When I update the event title to "Updated Title"
    Then the response status code should be 200
    And the response body should contain "Updated Title"

  # ------------------------------------------------------------------ #
  # Scenario 4: Delete an event from the hub                           #
  # ------------------------------------------------------------------ #
  Scenario: Delete an event from the hub
    When I create an event with title "Event To Delete" in city "Austin"
    Then the response status code should be 201
    And the event is created successfully
    When I delete the created event
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 5: Retrieve events filtered by category                   #
  # ------------------------------------------------------------------ #
  Scenario: Retrieve events filtered by category
    When I get events filtered by category "Technology"
    Then the response status code should be 200
    And the response body should not be empty
