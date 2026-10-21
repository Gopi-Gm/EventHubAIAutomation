Feature: EventHub Event Management

  Background:
    Given I am logged in as "prem@yopmail.com" with password "Automation@2026"

  # ------------------------------------------------------------------ #
  # Scenario 1: Retrieve all events
  # ------------------------------------------------------------------ #
  Scenario: Get all events
    When I request all events
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 2: Full event lifecycle - Create, Update, Delete
  # ------------------------------------------------------------------ #
  Scenario: Create update and delete an event
    When I create an event with title "Tech Summit 2026" in city "San Francisco"
    Then the response status code should be 201
    And the event is created successfully
    When I update the event title to "Tech Summit 2026 - Updated"
    Then the response status code should be 200
    When I delete the created event
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 3: Get event by ID
  # ------------------------------------------------------------------ #
  Scenario: Get event by ID
    When I create an event with title "Lookup Event" in city "New York"
    And the event is created successfully
    When I fetch the created event by ID
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 4: Get event with invalid ID returns not found
  # ------------------------------------------------------------------ #
  Scenario: Get event by invalid ID
    When I fetch event by ID "999999"
    Then the response status code should be 404

  # ------------------------------------------------------------------ #
  # Scenario 5: Search events by keyword
  # ------------------------------------------------------------------ #
  Scenario: Search events by keyword
    When I search events with query "technology"
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 6: Filter events by category
  # ------------------------------------------------------------------ #
  Scenario: Get events by category
    When I get events filtered by category "Technology"
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 7: Filter events by city
  # ------------------------------------------------------------------ #
  Scenario: Get events by city
    When I get events filtered by city "San Francisco"
    Then the response status code should be 200


