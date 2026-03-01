@regression
Feature: Google Translate Regression Suite

  @TC_TEXT_EN_SI
  Scenario: EN to SI translation
    Given I navigate to Google Translate
    And I select "Text" tab
    And I select "English" as source language
    And I select "Sinhala" as target language
    When I enter source text "Good morning. Please submit the report by 5 PM"
    Then translation should be displayed

  @TC_TEXT_EN_TA
  Scenario: EN to Tamil translation
    Given I navigate to Google Translate
    And I select "Text" tab
    And I select "English" as source language
    And I select "Tamil" as target language
    When I enter source text "Good morning. Please submit the report by 5 PM"
    Then translation should be displayed

  @TC_SWAP_EN_SI
  Scenario: Swap EN to SI
    Given I navigate to Google Translate
    And I select "Text" tab
    And I select "English" as source language
    And I select "Sinhala" as target language
    When I enter source text "Good morning"
    Then translation should be displayed
    When I click Swap

  @TC_IMAGE_EN_SI
  Scenario: Image EN to SI translation
    Given I navigate to Google Translate
    And I select "Images" tab
    And I select "English" as source language
    And I select "Sinhala" as target language
    When I upload file "en_image.png"
    Then Download button should be available
    When I click Download

  @TC_DOCUMENT_EN_SI
  Scenario: Document EN to SI translation
    Given I navigate to Google Translate
    And I select "Documents" tab
    And I select "English" as source language
    And I select "Sinhala" as target language
    When I upload file "en_doc.pdf"
    Then Download button should be available
    When I click Download

  @TC_WEBSITE_EN_SI
  Scenario: Website EN to SI translation
    Given I navigate to Google Translate
    And I select "Websites" tab
    And I select "English" as source language
    And I select "Sinhala" as target language
    When I enter website URL "https://example.com"
    And I click the arrow icon
    Then translated website should be shown