package ru.womantest.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.womantest.pages.LoginPage;
import ru.womantest.util.ConfigReader;

import java.time.Duration;

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
        if (d != null) {
            d.quit();
            driverHolder.remove();
            waitHolder.remove();
        }
    }
}
