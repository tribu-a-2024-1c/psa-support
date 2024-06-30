Feature: Ticket Assignment

  Scenario: Successfully assign a resource to a ticket
    Given there is an existing ticket with ID 123
    And a resource with details:
      | legajo | nombre   | apellido |
      | 321    | "John"   | "Doe"    |
    When I send a request to assign this resource to the ticket
    Then the response status should be 200
    And the ticket should have the resource assigned

  Scenario: Attempt to assign a resource to a non-existent ticket
    Given there is no ticket with ID 999
    And a resource with details:
      | legajo | nombre   | apellido |
      | 321    | "John"   | "Doe"    |
    When I send a request to assign this resource to the ticket
    Then the response status should be 404
    And the response should include an error message "Ticket not found"

