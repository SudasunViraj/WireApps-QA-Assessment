package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = DriverFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new RuntimeException("config.properties not found in src/test/resources");
            }
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

        if (!"chrome".equals(browser)) {
            throw new IllegalArgumentException("This simple framework supports chrome only. Set browser=chrome");
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        WebDriver driver = new ChromeDriver(options);

        // Maximize only in headed mode
        if (!headless) {
            driver.manage().window().maximize();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

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