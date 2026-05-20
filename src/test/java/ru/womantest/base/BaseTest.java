package ru.womantest.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.ForumLoginPage;
import ru.womantest.pages.LoginPage;
import ru.womantest.util.ConfigReader;

import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseTest {

    private static final Logger log = Logger.getLogger(BaseTest.class.getName());

    private static final ThreadLocal<Boolean> managedExternallyHolder = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitHolder = new ThreadLocal<>();
    private static final ThreadLocal<Thread> consentWatcherHolder = new ThreadLocal<>();

    private static final By CONSENT_BTN = By.xpath(
            "//button[normalize-space()='Consent']"
                    + " | //button[normalize-space()='Accept all']"
                    + " | //button[normalize-space()='Принять все']"
    );

    protected static final String BASE_URL = "https://www.woman.ru";

    protected WebDriver driver() {
        return driverHolder.get();
    }

    protected void setManagedExternally() {
        managedExternallyHolder.set(true);
    }

    protected void quitDriver() {
        stopConsentWatcher();
        WebDriver driver = driverHolder.get();
        if (driver != null) {
            driver.quit();
        }
        driverHolder.remove();
        waitHolder.remove();
        managedExternallyHolder.remove();
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
                    "--start-maximized",
                    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
            );
            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            options.setExperimentalOption("useAutomationExtension", false);
            driver = new ChromeDriver(options);
            ((JavascriptExecutor) driver).executeScript(
                    "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));
        driverHolder.set(driver);
        waitHolder.set(new WebDriverWait(driver, Duration.ofSeconds(20)));
        openUrl(driver, BASE_URL);
        handleCaptchaIfPresent(driverHolder.get());
        dismissPopupsIfPresent(driverHolder.get());
        startConsentWatcher(driver);
    }

    protected void initDriverAndLogin(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email и test.password, авторизационный тест пропущен");
        new LoginPage(driver(), getWait()).login(cfg.getEmail(), cfg.getPassword());
    }

    protected void initDriverAndLoginForum(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email и test.password, авторизационный тест пропущен");
        new ForumListPage(driver(), getWait()).open();
        new ForumLoginPage(driver(), getWait()).login(cfg.getEmail(), cfg.getPassword());
    }

    private void startConsentWatcher(WebDriver driver) {
        Thread watcher = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    List<WebElement> btns = driver.findElements(CONSENT_BTN);
                    for (WebElement btn : btns) {
                        if (btn.isDisplayed()) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                            return;
                        }
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception ignored) {
                }
            }
        });
        watcher.setDaemon(true);
        watcher.start();
        consentWatcherHolder.set(watcher);
    }

    private void stopConsentWatcher() {
        Thread watcher = consentWatcherHolder.get();
        if (watcher != null) {
            watcher.interrupt();
        }
        consentWatcherHolder.remove();
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

    private void dismissPopupsIfPresent(WebDriver driver) {
        dismissIfPresent(driver, By.xpath(
                "//div[contains(@class,'cookiesPopup')]//button[contains(normalize-space(.),'Согласен')]"
                        + " | //button[contains(@class,'cookie') and contains(normalize-space(.),'Согласен')]"
        ));
        dismissIfPresent(driver, CONSENT_BTN);
    }

    private void dismissIfPresent(WebDriver driver, By locator) {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(4))
                    .until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception ignored) {}
    }

    protected void openUrl(String url) {
        openUrl(driver(), url);
    }

    private void openUrl(WebDriver driver, String url) {
        try {
            driver.get(url);
        } catch (TimeoutException ignored) {
            ((JavascriptExecutor) driver).executeScript("window.stop();");
        }
    }

    private void handleCaptchaIfPresent(WebDriver driver) {
        if (!isCaptchaPage(driver)) return;

        log.warning("Обнаружена CAPTCHA.");
        boolean clicked = trySolveCaptchaCheckbox(driver);
        Assumptions.assumeTrue(clicked,
                "Не удалось найти элемент галочки на странице CAPTCHA.");

        try {
            new WebDriverWait(driver, Duration.ofSeconds(25))
                    .until(d -> !isCaptchaPage(d));
            log.info("CAPTCHA пройдена.");
        } catch (org.openqa.selenium.TimeoutException e) {
            Assumptions.assumeTrue(false,
                    "CAPTCHA не исчезла после нажатия галочки.");
        }
    }

    private boolean isCaptchaPage(WebDriver driver) {
        try {
            if (!driver.findElements(By.id("robotCheck")).isEmpty()
                    || !driver.findElements(By.id("captchaForm")).isEmpty()) {
                return true;
            }
            String title = driver.getTitle().toLowerCase();
            if (title.contains("проверяем соединение") || title.contains("captcha")
                    || title.contains("checking") || title.contains("attention required")) {
                return true;
            }
            String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
            return body.contains("не робот") || body.contains("checking your browser")
                    || body.contains("enable javascript and cookies")
                    || body.contains("ddos-guard");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean trySolveCaptchaCheckbox(WebDriver driver) {
        try {
            WebElement checkbox = driver.findElement(By.id("robotCheck"));
            new Actions(driver)
                    .moveToElement(checkbox)
                    .pause(Duration.ofMillis(400))
                    .click(checkbox)
                    .perform();
            log.info("Кликнул #robotCheck через Actions.");
            return true;
        } catch (Exception ignored) {}

        By genericCheckbox = By.xpath(
                "//input[@type='checkbox']"
                        + " | //div[contains(@class,'captcha-box')]"
        );
        try {
            List<WebElement> hits = driver.findElements(genericCheckbox);
            if (!hits.isEmpty()) {
                new Actions(driver)
                        .moveToElement(hits.get(0))
                        .pause(Duration.ofMillis(400))
                        .click(hits.get(0))
                        .perform();
                log.info("Кликнул captcha-элемент через Actions.");
                return true;
            }
        } catch (Exception ignored) {}

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            try {
                driver.switchTo().frame(iframe);
                List<WebElement> hits = driver.findElements(
                        By.xpath("//input[@type='checkbox'] | //div[contains(@class,'check')]"));
                if (!hits.isEmpty()) {
                    new Actions(driver)
                            .moveToElement(hits.get(0))
                            .pause(Duration.ofMillis(400))
                            .click(hits.get(0))
                            .perform();
                    driver.switchTo().defaultContent();
                    log.info("Кликнул галочку внутри iframe.");
                    return true;
                }
            } catch (Exception ignored) {
            } finally {
                try { driver.switchTo().defaultContent(); } catch (Exception ignored) {}
            }
        }

        return false;
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (Boolean.TRUE.equals(managedExternallyHolder.get())) {
            return;
        }
        stopConsentWatcher();
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
