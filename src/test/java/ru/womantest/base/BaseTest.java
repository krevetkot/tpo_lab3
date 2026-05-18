package ru.womantest.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.womantest.pages.LoginPage;
import ru.womantest.util.ConfigReader;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    private static final Map<String, WebDriver> sharedDrivers = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            sharedDrivers.values().forEach(d -> {
                try { d.quit(); } catch (Exception ignored) {}
            })
        ));
    }

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
        String key = browser.toLowerCase();
        WebDriver driver = sharedDrivers.get(key);
        if (driver == null) {
            if ("firefox".equalsIgnoreCase(browser)) {
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(new FirefoxOptions());
            } else {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments("--disable-blink-features=AutomationControlled");
                driver = new ChromeDriver(opts);
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            sharedDrivers.put(key, driver);
        } else {
            driver.switchTo().newWindow(WindowType.TAB);
        }
        driverHolder.set(driver);
        waitHolder.set(new WebDriverWait(driver, Duration.ofSeconds(15)));
        driver.get(BASE_URL);
    }

    protected void initDriverAndLogin(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        new LoginPage(driver(), getWait()).login(cfg.getEmail(), cfg.getPassword());
    }

    @AfterEach
    void tearDown() {
        WebDriver d = driverHolder.get();
        if (d != null && d.getWindowHandles().size() > 1) {
            d.close();
            d.switchTo().window(d.getWindowHandles().iterator().next());
        }
        driverHolder.remove();
        waitHolder.remove();
    }
}
