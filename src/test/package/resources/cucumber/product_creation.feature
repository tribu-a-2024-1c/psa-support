Feature: Product Creation

  Scenario: Successfully create a new product with version and clients
    Given I have the following product details:
      | name        | version | clients                                  |
      | "Software"  | "v1.0"  | [{"id": 1, "companyName": "ABC Inc."}]   |
    When I send a request to create a new product
    Then the response status should be 201
    And the product should be created with the above details

  Scenario: Attempt to create a product with an existing version
    Given there is already a product with the same version "v1.0"
    When I send a request to create a new product with the same version
    Then the response status should be 400
    And the response should include an error message "The version already exists for this product"

