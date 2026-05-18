package ru.womantest.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.womantest.pages.LoginPage;
import ru.womantest.util.ConfigReader;

import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitHolder = new ThreadLocal<>();

    protected static final String BASE_URL = "https://www.woman.ru";

    protected WebDriver driver() {
        return driverHolder.get();
    }

    protected WebDriverWait getWait() {
        return waitHolder.get();
    }

    protected void initDriver(String browser) {
        WebDriver driver;
        if ("firefox".equalsIgnoreCase(browser)) {
            String firefoxBinary = firefoxBinary();
            Assumptions.assumeTrue(firefoxBinary != null,
                "Firefox не найден. Установите Firefox или задайте путь через -Dfirefox.binary=...");
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            options.setBinary(firefoxBinary);
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);
            options.addPreference("dom.webnotifications.enabled", false);
            driver = new FirefoxDriver(options);
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);
            options.addArguments(
                "--disable-blink-features=AutomationControlled",
                "--disable-notifications",
                "--remote-allow-origins=*",
                "--start-maximized"
            );
            driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
        driverHolder.set(driver);
        waitHolder.set(new WebDriverWait(driver, Duration.ofSeconds(20)));
        driver.get(BASE_URL);
    }

    protected void initDriverAndLogin(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
            "Нет test.properties с test.email и test.password, авторизационный тест пропущен");
        new LoginPage(driver(), getWait()).login(cfg.getEmail(), cfg.getPassword());
    }

    private String firefoxBinary() {
        String propertyValue = System.getProperty("firefox.binary");
        if (isExistingFile(propertyValue)) {
            return propertyValue;
        }

        String envValue = System.getenv("FIREFOX_BIN");
        if (isExistingFile(envValue)) {
            return envValue;
        }

        String[] candidates = {
            "C:\\Program Files\\Mozilla Firefox\\firefox.exe",
            "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"
        };
        for (String candidate : candidates) {
            if (isExistingFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean isExistingFile(String path) {
        return path != null && !path.isBlank() && Files.isRegularFile(Path.of(path));
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        WebDriver driver = driverHolder.get();
        if (driver != null) {
            dumpFailureArtifacts(driver, testInfo);
            driver.quit();
        }
        driverHolder.remove();
        waitHolder.remove();
    }

    private void dumpFailureArtifacts(WebDriver driver, TestInfo testInfo) {
        try {
            Path dir = Path.of("build", "selenium-debug");
            Files.createDirectories(dir);
            String name = testInfo.getTestMethod()
                .map(method -> method.getName())
                .orElse("test");
            Files.writeString(dir.resolve(name + ".html"), driver.getPageSource());
            if (driver instanceof TakesScreenshot screenshotDriver) {
                Files.write(dir.resolve(name + ".png"), screenshotDriver.getScreenshotAs(OutputType.BYTES));
            }
        } catch (IOException | RuntimeException ignored) {
        }
    }
}
