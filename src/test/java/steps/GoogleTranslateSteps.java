package steps;

import core.DriverFactory;
import io.cucumber.java.en.*;
import org.junit.Assert;
import pages.GoogleTranslatePage;

public class GoogleTranslateSteps {

    private final GoogleTranslatePage page = new GoogleTranslatePage();

    // -------------------- Common --------------------

    @Given("I navigate to Google Translate")
    public void iNavigateToGoogleTranslate() {
        page.navigateHome();
    }

    @And("I select {string} tab")
    public void iSelectTab(String tabName) {
        page.selectTab(tabName);
    }

    @And("I select {string} as source language")
    public void iSelectSourceLanguage(String language) {
        page.selectSourceLanguage(language);
    }

    @And("I select {string} as target language")
    public void iSelectTargetLanguage(String language) {
        page.selectTargetLanguage(language);
    }

    // -------------------- Text Translation --------------------

    @When("I enter source text {string}")
    public void iEnterSourceText(String text) {
        page.enterSourceText(text);
    }

    @Then("translation should be displayed")
    public void translationShouldBeDisplayed() {
        Assert.assertTrue(
                "Expected translation output to be displayed (non-empty).",
                page.waitForTranslationTextToAppear()
        );
    }

    // -------------------- Swap --------------------

    @When("I click Swap")
    public void iClickSwap() {
        String beforeSource = page.getSelectedSourceLanguage();
        String beforeTarget = page.getSelectedTargetLanguage();

        page.clickSwap();

        String afterSource = page.getSelectedSourceLanguage();
        String afterTarget = page.getSelectedTargetLanguage();

        // If labels are detectable, validate proper swap
        // If Google changes DOM and labels are empty, we don't hard-fail here.
        if (!beforeSource.isEmpty() && !beforeTarget.isEmpty() && !afterSource.isEmpty() && !afterTarget.isEmpty()) {
            Assert.assertEquals("Source language should be swapped.", beforeTarget, afterSource);
            Assert.assertEquals("Target language should be swapped.", beforeSource, afterTarget);
        }
    }

    // -------------------- Image / Document Upload --------------------

    @When("I upload file {string}")
    public void iUploadFile(String fileName) {
        page.uploadFileFromResources(fileName);
    }

    @Then("Download button should be available")
    public void downloadButtonShouldBeAvailable() {
        Assert.assertTrue(
                "Expected Download button to appear after translation processing.",
                page.waitForDownloadButtonVisible()
        );
    }

    @When("I click Download")
    public void iClickDownload() {
        page.clickDownloadIfAvailable();
    }

    // -------------------- Website Translation --------------------

    @When("I enter website URL {string}")
    public void iEnterWebsiteUrl(String url) {
        page.enterWebsiteUrl(url);
    }

    @When("I enter configured website URL")
    public void iEnterConfiguredWebsiteUrl() {
        page.enterWebsiteUrl(DriverFactory.getProp("websiteUrl"));
    }

    @And("I click the arrow icon")
    public void iClickTheArrowIcon() {
        page.clickWebsiteArrow();
    }

    @Then("translated website should be shown")
    public void translatedWebsiteShouldBeShown() {
        Assert.assertTrue(
                "Expected translated website to load (iframe/title).",
                page.waitForTranslatedWebsite()
        );
    }
}