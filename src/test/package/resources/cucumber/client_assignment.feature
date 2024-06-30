Feature: Client Assignment

  Scenario: Successfully assign a client to a product
    Given there is an existing product with ID 100
    And a client with details:
      | id          | companyName | cuit         |
      | 200         | "XYZ Corp"  | "30-71000001-2" |
    When I send a request to assign this client to the product
    Then the response status should be 200
    And the client should be assigned to the product

  Scenario: Attempt to assign a client to a non-existent product
    Given there is no product with ID 999
    And a client with details:
      | id          | companyName | cuit         |
      | 200         | "XYZ Corp"  | "30-71000001-2" |
    When I send a request to assign this client to the product
    Then the response status should be 404
    And the response should include an error message "Product not found"

