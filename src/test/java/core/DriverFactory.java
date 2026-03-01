package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = DriverFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) throw new RuntimeException("config.properties not found under src/test/resources");
            PROPS.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProp(String key) {
        return System.getProperty(key, PROPS.getProperty(key));
    }

    public static int getIntProp(String key) {
        return Integer.parseInt(getProp(key));
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void initDriver() {
        String browser = getProp("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(getProp("headless"));

        WebDriver driver;

        switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) options.addArguments("-headless");
                driver = new FirefoxDriver(options);
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--window-size=1400,900");
                options.addArguments("--disable-blink-features=AutomationControlled");
                driver = new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        DRIVER.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}