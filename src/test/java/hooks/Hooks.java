package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;

import static java.nio.file.Files.*;
import static core.DriverFactory.*;

public class Hooks {

    @Before
    public void beforeScenario() {
        initDriver();
        WebDriver driver = getDriver();
        driver.get(getProp("baseUrl"));
    }

    @After
    public void afterScenario(Scenario scenario) {
        WebDriver driver = getDriver();

        if (scenario.isFailed() && driver != null) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
            try {
                Path dir = Path.of("target", "screenshots");
                createDirectories(dir);
                String safe = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
                write(dir.resolve(safe + ".png"), screenshot);
            } catch (Exception ignored) {}
        }

        quitDriver();
    }
}