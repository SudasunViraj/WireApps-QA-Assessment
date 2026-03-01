package steps;

import io.cucumber.java.en.*;
import org.junit.Assert;
import pages.GoogleTranslatePage;

import static core.DriverFactory.getProp;

public class RegressionSteps {

    private final GoogleTranslatePage page = new GoogleTranslatePage();

    // ---------- Text translation ----------
    @Given("I open text translate from {string} to {string}")
    public void i_open_text_translate_from_to(String from, String to) {
        page.openTextTranslate(from, to);
    }

    @When("I translate the text {string}")
    public void i_translate_the_text(String text) {
        page.enterSourceText(text);
    }

    @Then("I should see translation output")
    public void i_should_see_translation_output() {
        Assert.assertTrue(
                "Expected some translation output to appear (non-empty).",
                page.waitForAnyTranslationOutput()
        );
    }

    // ---------- Swap ----------
    @When("I swap the languages")
    public void i_swap_the_languages() {
        page.clickSwap();
    }

    @Then("translation still works for text {string}")
    public void translation_still_works_for_text(String text) {
        page.clearSource();
        page.enterSourceText(text);

        Assert.assertTrue(
                "Expected translation output after swapping languages.",
                page.waitForAnyTranslationOutput()
        );
    }

    // ---------- Images ----------
    @Given("I open images translate from {string} to {string}")
    public void i_open_images_translate_from_to(String from, String to) {
        page.openTextTranslate(from, to);
        page.goToImagesTab();
    }

    @When("I upload image {string} for translation")
    public void i_upload_image_for_translation(String resourceFile) {
        page.uploadImageFromResources("testdata/" + resourceFile);
    }

    // ---------- Documents ----------
    @Given("I open documents translate from {string} to {string}")
    public void i_open_documents_translate_from_to(String from, String to) {
        page.openTextTranslate(from, to);
        page.goToDocumentsTab();
    }

    @When("I upload document {string} for translation")
    public void i_upload_document_for_translation(String resourceFile) {
        page.uploadDocumentFromResources("testdata/" + resourceFile);
    }

    @Then("I should be able to download the translated document")
    public void i_should_be_able_to_download_the_translated_document() {
        Assert.assertTrue(
                "Expected a download button/link for translated document to appear.",
                page.waitForDownloadButton()
        );
    }

    // ---------- Website ----------
    @Given("I translate website from {string} to {string}")
    public void i_translate_website_from_to(String from, String to) {
        String website = getProp("websiteUrl");
        page.openWebsiteTranslation(from, to, website);
    }

    @Then("the translated website should load")
    public void the_translated_website_should_load() {
        Assert.assertTrue(
                "Expected translated website to load (iframe/content).",
                page.waitForTranslatedWebsiteToLoad()
        );
    }
}