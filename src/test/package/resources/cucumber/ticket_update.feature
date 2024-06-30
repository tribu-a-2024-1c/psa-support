Feature: Ticket Update

  Scenario: Successfully update an existing ticket
    Given there is an existing ticket with ID 123
    And I have the following new ticket details to update:
      | title       | status   | severity | description          |
      | "Issue 123" | "Closed" | "Medium" | "Issue resolved"     |
    When I send a request to update the ticket
    Then the response status should be 200
    And the ticket should be updated with the new details

  Scenario: Attempt to update a non-existent ticket
    Given there is no ticket with ID 999
    When I send a request to update the ticket
    Then the response status should be 404
    And the response should include an error message "Ticket not found"

