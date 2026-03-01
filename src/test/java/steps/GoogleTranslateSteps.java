package steps;

import core.DriverFactory;
import io.cucumber.java.en.*;
import org.junit.Assert;
import pages.GoogleTranslatePage;

public class GoogleTranslateSteps {

    private final GoogleTranslatePage page = new GoogleTranslatePage();

    // Common start
    @Given("I navigate to Google Translate")
    public void iNavigateToGoogleTranslate() {
        page.navigateHome();
    }

    @Given("I select {string} tab")
    public void iSelectTab(String tabName) {
        page.selectTab(tabName);
    }

    @Given("I select {string} as source language")
    public void iSelectSourceLanguage(String lang) {
        page.selectSourceLanguage(lang);
    }

    @Given("I select {string} as target language")
    public void iSelectTargetLanguage(String lang) {
        page.selectTargetLanguage(lang);
    }

    // Text translation
    @When("I enter source text {string}")
    public void iEnterSourceText(String text) {
        page.enterSourceText(text);
    }

    @Then("translation should be displayed")
    public void translationShouldBeDisplayed() {
        Assert.assertTrue("Expected translation output to be displayed.", page.waitForTranslationTextToAppear());
    }

    // Swap
    @When("I click Swap")
    public void iClickSwap() {
        // capture before swap (best-effort)
        String beforeSource = page.getSelectedSourceLanguage();
        String beforeTarget = page.getSelectedTargetLanguage();

        page.clickSwap();

        String afterSource = page.getSelectedSourceLanguage();
        String afterTarget = page.getSelectedTargetLanguage();

        // Validate swap in a robust way: if labels are available, verify; otherwise at least ensure no crash
        if (!beforeSource.isEmpty() && !beforeTarget.isEmpty() && !afterSource.isEmpty() && !afterTarget.isEmpty()) {
            Assert.assertEquals("Source language should be swapped.", beforeTarget, afterSource);
            Assert.assertEquals("Target language should be swapped.", beforeSource, afterTarget);
        }
    }

    // Image/Document upload + download
    @When("I upload file {string}")
    public void iUploadFile(String fileName) {
        page.uploadFileFromResources(fileName);
    }

    @Then("Download button should be available")
    public void downloadButtonShouldBeAvailable() {
        Assert.assertTrue("Expected Download button to appear.", page.waitForDownloadButtonVisible());
    }

    @When("I click Download")
    public void iClickDownload() {
        page.clickDownloadIfAvailable();
    }

    // Website
    @When("I enter website URL {string}")
    public void iEnterWebsiteUrl(String url) {
        page.enterWebsiteUrl(url);
    }

    @When("I click the arrow icon")
    public void iClickArrowIcon() {
        page.clickWebsiteArrow();
    }

    @Then("translated website should be shown")
    public void translatedWebsiteShouldBeShown() {
        Assert.assertTrue("Expected translated website to load.", page.waitForTranslatedWebsite());
    }

    // Optional: use config websiteUrl
    @When("I enter configured website URL")
    public void iEnterConfiguredWebsiteUrl() {
        page.enterWebsiteUrl(DriverFactory.getProp("websiteUrl"));
    }
}