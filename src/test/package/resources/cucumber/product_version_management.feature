Feature: Product Version Management

  Scenario: Successfully add a new version to an existing product
    Given there is an existing product with ID 100
    And I have a new version details:
      | version |
      | "v2.0"  |
    When I send a request to add this version to the product
    Then the response status should be 201
    And the product should have the new version added

  Scenario: Attempt to add a duplicate version to a product
    Given there is an existing product with ID 100
    And the product already has a version "v2.0"
    When I send a request to add the same version again
    Then the response status should be 400
    And the response should include an error message "The version already exists for this product"

