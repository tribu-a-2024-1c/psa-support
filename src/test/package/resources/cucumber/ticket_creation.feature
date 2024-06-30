Feature: Ticket Creation

  Scenario: Successfully creating a new ticket
    Given I have the following ticket details:
      | title       | description     | severity | startDate           | endDate             | status  | type   | productVersionId |
      | "Issue 101" | "Login failure" | "High"   | "2021-01-01T09:00Z" | "2021-01-02T17:00Z" | "Open"  | "Bug"  | 1001              |
    When I send a request to create a new ticket
    Then the response status should be 201
    And the ticket should be created with the details above

  Scenario: Attempt to create a ticket with missing mandatory fields
    Given I have the following ticket details missing mandatory fields:
      | title       | severity | startDate           |
      | "Issue 102" | "Low"    | "2021-01-01T09:00Z" |
    When I send a request to create a new ticket
    Then the response status should be 400
    And the response should include an error message "The ticket could not be created"

