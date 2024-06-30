Feature: Ticket Retrieval

  Scenario: Retrieve all tickets successfully
    Given there are multiple tickets created
    When I send a request to retrieve all tickets
    Then the response status should be 200
    And the response should include a list of all tickets

  Scenario: Retrieve a specific ticket by ID successfully
    Given there is a ticket with ID 123
    When I send a request to retrieve this ticket
    Then the response status should be 200
    And the ticket details should match the ticket with ID 123

  Scenario: Attempt to retrieve a non-existent ticket
    Given there is no ticket with ID 999
    When I send a request to retrieve this ticket
    Then the response status should be 404
    And the response should include an error message "Ticket not found"

