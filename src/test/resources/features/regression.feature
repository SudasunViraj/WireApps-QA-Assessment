@regression
Feature: Google Translate Regression Suite

  @TC_ENG_SI
  Scenario: Translate English sentence to Sinhala
    Given I open text translate from "en" to "si"
    When I translate the text "Good morning. Please submit the report by 5 PM."
    Then I should see translation output

  @TC_ENG_TA
  Scenario: Translate English sentence to Tamil
    Given I open text translate from "en" to "ta"
    When I translate the text "How are you today?"
    Then I should see translation output

  @TC_SWAP_EN_SI
  Scenario: Swap EN/SI languages reverses correctly
    Given I open text translate from "en" to "si"
    When I translate the text "Hello"
    Then I should see translation output
    When I swap the languages
    Then translation still works for text "Test after swap"

  @TC_IMG_SI_EN
  Scenario: Upload image with Sinhala text and translate to English
    Given I open images translate from "si" to "en"
    When I upload image "sinhala.png" for translation
    Then I should see translation output

  @TC_DOC_SI_EN
  Scenario: Upload document with Sinhala text and translate to English
    Given I open documents translate from "si" to "en"
    When I upload document "sinhala.docx" for translation
    Then I should be able to download the translated document

  @TC_WEB_EN_SI
  Scenario: Translate a valid English website to Sinhala
    Given I translate website from "en" to "si"
    Then the translated website should load