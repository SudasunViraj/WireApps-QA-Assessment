package pages;

import core.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.nio.file.*;
import java.time.Duration;

public class GoogleTranslatePage {

    private final WebDriver driver = DriverFactory.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DriverFactory.getIntProp("timeoutSeconds")));

    // ---------- Tabs ----------
    private By tabByName(String tab) {
        // Google often uses role=tab
        return By.xpath("//*[(@role='tab' or self::button) and normalize-space()='" + tab + "']");
    }

    // ---------- Language menus (tolerant) ----------
    private final By sourceLangMoreBtn = By.cssSelector("button[aria-label*='More source languages'], button[aria-label*='source language']");
    private final By targetLangMoreBtn = By.cssSelector("button[aria-label*='More target languages'], button[aria-label*='target language']");
    private final By languageSearchInput = By.cssSelector("input[aria-label*='Search languages'], input[aria-label*='Search']");

    // Selected language labels on the top bar (best-effort)
    private final By selectedSourceLangLabel = By.xpath("(//button[contains(@aria-label,'source') or contains(@aria-label,'Source')]//*[self::span or self::div])[last()]");
    private final By selectedTargetLangLabel = By.xpath("(//button[contains(@aria-label,'target') or contains(@aria-label,'Target')]//*[self::span or self::div])[last()]");

    // ---------- Text input/output ----------
    private final By sourceTextArea = By.cssSelector("textarea[aria-label]");
    private final By outputNonEmpty = By.cssSelector("div[aria-live='polite'] span");

    // ---------- Swap ----------
    private final By swapButton = By.cssSelector("button[aria-label*='Swap'], button[aria-label*='swap']");

    // ---------- Upload (Images/Documents) ----------
    private final By fileInput = By.cssSelector("input[type='file']");
    private final By downloadBtn = By.xpath("//button[contains(.,'Download') or contains(.,'download')] | //a[contains(.,'Download') or contains(.,'download')]");

    // ---------- Websites ----------
    private final By websiteUrlInput = By.cssSelector("input[type='url'], input[aria-label*='Website']");
    private final By websiteArrowBtn = By.cssSelector("button[aria-label*='Translate'], button[aria-label*='translate'], button[aria-label*='Go'], button[aria-label*='Arrow']");
    private final By anyIframe = By.cssSelector("iframe");

    // ---------- Consent / cookies ----------
    private final By consentButtons = By.xpath(
            "//button//*[contains(.,'Accept all') or contains(.,'I agree') or contains(.,'Agree') or contains(.,'Accept')]" +
            "/ancestor::button[1]"
    );

    public void navigateHome() {
        driver.get(DriverFactory.getProp("baseUrl"));
        acceptConsentIfPresent();
        waitForReady();
    }

    public void selectTab(String tabName) {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabByName(tabName)));
        tab.click();
        waitForReady();
        acceptConsentIfPresent();
    }

    public void selectSourceLanguage(String language) {
        openLanguageMenu(true);
        searchAndSelectLanguage(language);
    }

    public void selectTargetLanguage(String language) {
        openLanguageMenu(false);
        searchAndSelectLanguage(language);
    }

    public void enterSourceText(String text) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(sourceTextArea));
        input.click();
        input.clear();
        input.sendKeys(text);
    }

    public boolean waitForTranslationTextToAppear() {
        try {
            return wait.until(d -> d.findElements(outputNonEmpty).stream()
                    .map(WebElement::getText)
                    .anyMatch(t -> t != null && !t.trim().isEmpty()));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickSwap() {
        wait.until(ExpectedConditions.elementToBeClickable(swapButton)).click();
        waitForReady();
    }

    public String getSelectedSourceLanguage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(selectedSourceLangLabel)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getSelectedTargetLanguage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(selectedTargetLangLabel)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ---------- Images/Documents ----------
    public void uploadFileFromResources(String resourceRelativePathUnderTestdata) {
        Path filePath = extractResourceToTemp("testdata/" + resourceRelativePathUnderTestdata);
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(fileInput));
        input.sendKeys(filePath.toAbsolutePath().toString());
    }

    public void clickDownloadIfAvailable() {
        wait.until(ExpectedConditions.elementToBeClickable(downloadBtn)).click();
    }

    public boolean waitForDownloadButtonVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(downloadBtn));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------- Websites ----------
    public void enterWebsiteUrl(String url) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(websiteUrlInput));
        input.click();
        input.clear();
        input.sendKeys(url);
    }

    public void clickWebsiteArrow() {
        wait.until(ExpectedConditions.elementToBeClickable(websiteArrowBtn)).click();
    }

    public boolean waitForTranslatedWebsite() {
        try {
            return wait.until(d -> !d.findElements(anyIframe).isEmpty() || d.getTitle() != null);
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------- internal helpers ----------
    private void openLanguageMenu(boolean isSource) {
        By btn = isSource ? sourceLangMoreBtn : targetLangMoreBtn;
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        acceptConsentIfPresent();
    }

    private void searchAndSelectLanguage(String language) {
        // Search field
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(languageSearchInput));
        search.click();
        search.clear();
        search.sendKeys(language);

        // Select the language option - best effort and tolerant
        By option = By.xpath(
                "//*[@role='option' or @role='menuitem' or @data-language-code]" +
                "[.//*[normalize-space()='" + language + "'] or normalize-space()='" + language + "']"
        );

        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
        waitForReady();
    }

    private void acceptConsentIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(consentButtons));
            btn.click();
        } catch (Exception ignored) {
            // no consent popup
        }
    }

    private void waitForReady() {
        try {
            wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        } catch (Exception ignored) {}
    }

    private static Path extractResourceToTemp(String resourcePath) {
        try (InputStream is = GoogleTranslatePage.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Test data not found: src/test/resources/" + resourcePath);
            }
            String fileName = Path.of(resourcePath).getFileName().toString();
            Path temp = Files.createTempFile("gt_", "_" + fileName);
            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
            temp.toFile().deleteOnExit();
            return temp;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + resourcePath, e);
        }
    }
}