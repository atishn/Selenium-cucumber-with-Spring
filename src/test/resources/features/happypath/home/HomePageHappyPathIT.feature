# For reference I have take very simple case. But with existing SeleniumAPI class from codebase,
#you can implement various scenarios.

@HomePage @happypath
Feature: Yahoo Home Page
  As a user, I can load Yahoo Home Page and verify its been working as specified

  @ready
  Scenario: As a User I can successfully go to Yahoo home page and verify its title as 'Yahoo'
    Given User browse to Yahoo home page
    Then Title of the page should be Yahoo


