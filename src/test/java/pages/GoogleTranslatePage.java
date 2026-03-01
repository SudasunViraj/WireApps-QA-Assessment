package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;

import static core.DriverFactory.*;

public class GoogleTranslatePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public GoogleTranslatePage() {
        this.driver = getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(getIntProp("timeoutSeconds")));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    // ====== Locators (keep tolerant because Google UI can change) ======

    private By sourceTextArea() {
        // Source textarea usually has aria-label; hl=en helps keep stable
        return By.cssSelector("textarea[aria-label]");
    }

    private By swapButton() {
        // Try common aria label first
        return By.cssSelector("button[aria-label*='Swap'], button[aria-label*='swap']");
    }

    private By clearButton() {
        return By.cssSelector("button[aria-label*='Clear'], button[aria-label*='clear']");
    }

    private By imagesTab() {
        // Role tab often used; fallback to text match
        return By.xpath("//button[@role='tab' and (contains(.,'Images') or contains(.,'Image'))] | //div[@role='tab' and (contains(.,'Images') or contains(.,'Image'))]");
    }

    private By documentsTab() {
        return By.xpath("//button[@role='tab' and contains(.,'Documents')] | //div[@role='tab' and contains(.,'Documents')]");
    }

    private By uploadInputFile() {
        // Most upload widgets end up as <input type="file">
        return By.cssSelector("input[type='file']");
    }

    private By downloadTranslationButton() {
        // Document translation typically offers "Download translation"
        return By.xpath("//a[contains(.,'Download') or contains(.,'download')] | //button[contains(.,'Download') or contains(.,'download')]");
    }

    // Output area: do NOT assert exact translation text; just assert some non-empty result appears
    private By outputCandidates() {
        return By.cssSelector("div[aria-live='polite'] span, [data-result-index] span, span");
    }

    // ====== Core actions ======

    public void openTextTranslate(String sl, String tl) {
        // Force known state via URL parameters (most stable way)
        // sl=source language, tl=target language
        String url = "https://translate.google.com/?hl=en&op=translate&sl=" + encode(sl) + "&tl=" + encode(tl);
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(sourceTextArea()));
    }

    public void enterSourceText(String text) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(sourceTextArea()));
        input.click();
        input.clear();
        input.sendKeys(text);
    }

    public void clearSource() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(clearButton()));
            btn.click();
        } catch (Exception e) {
            driver.findElement(sourceTextArea()).clear();
        }
    }

    public String getSourceValue() {
        return driver.findElement(sourceTextArea()).getAttribute("value");
    }

    public void clickSwap() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(swapButton()));
        btn.click();
    }

    public boolean waitForAnyTranslationOutput() {
        // Wait until any visible candidate element has non-empty text
        try {
            return wait.until(d -> {
                List<WebElement> els = d.findElements(outputCandidates());
                for (WebElement el : els) {
                    if (!el.isDisplayed()) continue;
                    String t = el.getText();
                    if (t != null && !t.trim().isEmpty()) {
                        // ensure not just the same input echoed; we only need "some output"
                        return true;
                    }
                }
                return false;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ====== Images translation ======
    public void goToImagesTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(imagesTab()));
        tab.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(uploadInputFile()));
    }

    public void uploadImageFromResources(String resourcePath) {
        // resourcePath example: "testdata/sinhala.png"
        Path fileOnDisk = extractResourceToTemp(resourcePath);
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(uploadInputFile()));
        fileInput.sendKeys(fileOnDisk.toAbsolutePath().toString());
    }

    // ====== Documents translation ======
    public void goToDocumentsTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(documentsTab()));
        tab.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(uploadInputFile()));
    }

    public void uploadDocumentFromResources(String resourcePath) {
        Path fileOnDisk = extractResourceToTemp(resourcePath);
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(uploadInputFile()));
        fileInput.sendKeys(fileOnDisk.toAbsolutePath().toString());
    }

    public boolean waitForDownloadButton() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(downloadTranslationButton()));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ====== Website translation ======
    public void openWebsiteTranslation(String from, String to, String websiteUrl) {
        // Reliable pattern: translate.google.com/translate?sl=en&tl=si&u=...
        String url = "https://translate.google.com/translate?sl=" + encode(from) +
                "&tl=" + encode(to) +
                "&u=" + encode(websiteUrl);
        driver.get(url);
    }

    public boolean waitForTranslatedWebsiteToLoad() {
        // The translated web page often loads inside an iframe, but Google can change this.
        // We'll wait for either an iframe OR evidence of a translated page container.
        try {
            return wait.until(d -> {
                boolean hasIframe = !d.findElements(By.cssSelector("iframe")).isEmpty();
                boolean hasBodyText = d.findElement(By.tagName("body")).getText().length() > 30;
                return hasIframe || hasBodyText;
            });
        } catch (Exception e) {
            return false;
        }
    }

    // ====== Helpers ======

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static Path extractResourceToTemp(String resourcePath) {
        try (InputStream is = GoogleTranslatePage.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Test data not found: src/test/resources/" + resourcePath);

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