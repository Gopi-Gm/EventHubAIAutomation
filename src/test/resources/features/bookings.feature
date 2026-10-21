Feature: EventHub Booking Management

  Background:
    Given I am logged in as "prem@yopmail.com" with password "Automation@2026"

  # ------------------------------------------------------------------ #
  # Scenario 1: Retrieve all bookings for the logged-in user
  # ------------------------------------------------------------------ #
  Scenario: Get all bookings
    When I request all bookings
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 2: Full booking lifecycle - Create, Update, Cancel
  # ------------------------------------------------------------------ #
  Scenario: Create update and cancel a booking
    Given an event exists to book
    When I create a booking for 2 tickets
    Then the response status code should be 201
    And the booking is created successfully
    When I cancel the booking
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 3: Get booking by ID
  # ------------------------------------------------------------------ #
  Scenario: Get booking by ID
    Given an event exists to book
    When I create a booking for 1 tickets
    Then the response status code should be 201
    And the booking is created successfully
    When I fetch the created booking by ID
    Then the response status code should be 200

  # ------------------------------------------------------------------ #
  # Scenario 4: Get booking with invalid ID returns not found
  # ------------------------------------------------------------------ #
  Scenario: Get booking by invalid ID
    When I fetch booking by ID "999999"
    Then the response status code should be 404
