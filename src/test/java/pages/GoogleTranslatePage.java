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
    private final WebDriverWait wait =
            new WebDriverWait(driver, Duration.ofSeconds(DriverFactory.getIntProp("timeoutSeconds")));

    // ---------- Tabs ----------
    private By tabByName(String tab) {
        return By.xpath("//*[(@role='tab' or self::button) and normalize-space()='" + tab + "']");
    }

    // ---------- Language menus ----------
    private final By sourceLangMoreBtn =
            By.cssSelector("button[aria-label*='More source languages'], button[aria-label*='source language']");
    private final By targetLangMoreBtn =
            By.cssSelector("button[aria-label*='More target languages'], button[aria-label*='target language']");

    // Language chooser container
    private final By langDialog = By.cssSelector("div[role='dialog'], div[role='listbox']");

    // Search inputs (varies a lot)
    private final By[] languageSearchInputs = new By[]{
            By.cssSelector("div[role='dialog'] input[type='search']"),
            By.cssSelector("div[role='dialog'] input[aria-label*='Search']"),
            By.cssSelector("div[role='dialog'] input[role='combobox']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[aria-label*='Search']")
    };

    // Selected language labels
    private final By selectedSourceLangLabel =
            By.xpath("(//button[contains(@aria-label,'source') or contains(@aria-label,'Source')]//*[self::span or self::div])[last()]");
    private final By selectedTargetLangLabel =
            By.xpath("(//button[contains(@aria-label,'target') or contains(@aria-label,'Target')]//*[self::span or self::div])[last()]");

    // ---------- Text input/output ----------
    private final By sourceTextArea = By.cssSelector("textarea[aria-label]");
private final By translationResult = By.cssSelector("div[data-result-index], span[jsname='W297wb'], div[jsname='W297wb']");
    // ---------- Swap ----------
    private final By swapButton = By.cssSelector("button[aria-label*='Swap'], button[aria-label*='swap']");

    // ---------- Upload (Images/Documents) ----------
    private final By fileInput = By.cssSelector("input[type='file']");
    private final By downloadBtn =
            By.xpath("//button[contains(.,'Download') or contains(.,'download')] | //a[contains(.,'Download') or contains(.,'download')]");

    // ---------- Websites ----------
    private final By websiteUrlInput = By.cssSelector("input[type='url'], input[aria-label*='Website']");
    private final By websiteArrowBtn =
            By.cssSelector("button[aria-label*='Translate'], button[aria-label*='translate'], button[aria-label*='Go'], button[aria-label*='Arrow']");
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
        acceptConsentIfPresent();
        waitForReady();
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
        return wait.until(driver -> {
            for (WebElement el : driver.findElements(translationResult)) {
                String text = el.getText();
                if (text != null && !text.trim().isEmpty()) {
                    return true;
                }
            }
            return false;
        });
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
            return wait.until(d -> !d.findElements(anyIframe).isEmpty() || (d.getTitle() != null && !d.getTitle().isEmpty()));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------- internal helpers ----------

    private void openLanguageMenu(boolean isSource) {
        By btn = isSource ? sourceLangMoreBtn : targetLangMoreBtn;

        WebElement menuBtn = wait.until(ExpectedConditions.elementToBeClickable(btn));
        try {
            menuBtn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(langDialog));
        acceptConsentIfPresent();
    }

    private void searchAndSelectLanguage(String language) {
        WebElement dialog = wait.until(ExpectedConditions.presenceOfElementLocated(langDialog));

        // Try using the search box if it exists (optional)
        WebElement searchBox = null;
        for (By by : languageSearchInputs) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                WebElement el = shortWait.until(ExpectedConditions.visibilityOfElementLocated(by));
                if (el.isDisplayed() && el.isEnabled()) {
                    searchBox = el;
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        if (searchBox != null) {
            try {
                searchBox.click();
                searchBox.clear();
                searchBox.sendKeys(language);
            } catch (Exception ignored) {}
        }

        // Select by language code
        String code = languageToCode(language);

        if (code != null) {
            By byCode = By.cssSelector("[data-language-code='" + code + "']");
            if (clickIfPresent(dialog, byCode)) {
                waitForReady();
                return;
            }
            // sometimes not under dialog, try global
            if (clickIfPresent(driver, byCode)) {
                waitForReady();
                return;
            }
        }

        // Fallback 1: contains match inside dialog (tolerant)
        By byContainsInDialog = By.xpath(".//*[contains(normalize-space(),'" + language + "')]");
        if (clickIfPresent(dialog, byContainsInDialog)) {
            waitForReady();
            return;
        }

        // Fallback 2: exact match global
        By byExactGlobal = By.xpath("//*[normalize-space()='" + language + "']");
        if (clickIfPresent(driver, byExactGlobal)) {
            waitForReady();
            return;
        }

        throw new TimeoutException("Could not select language: " + language +
                " (code=" + code + "). The language option was not clickable/visible.");
    }

    private boolean clickIfPresent(SearchContext context, By locator) {
        try {
            WebElement el = context.findElement(locator);
            scrollIntoView(el);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(el)).click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void scrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        } catch (Exception ignored) {}
    }

    private String languageToCode(String language) {
        if (language == null) return null;
        String l = language.trim().toLowerCase();
        switch (l) {
            case "english":
                return "en";
            case "sinhala":
                return "si";
            case "tamil":
                return "ta";
            default:
                return null; // fallback to text selection
        }
    }

    private void acceptConsentIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(consentButtons));
            btn.click();
        } catch (Exception ignored) {
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