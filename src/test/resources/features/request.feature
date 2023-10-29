Feature: Request work

  Scenario: Request work
    Given TODAY's block A

    When work is requested
    And work is generated

    Then work is sent to client