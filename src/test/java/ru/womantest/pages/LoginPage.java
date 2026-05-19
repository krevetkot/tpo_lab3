package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage {

    private final By loginBtn = By.xpath(
        "/html/body/div[1]/div[2]/header/div[1]/div[1]/div/noindex/div/button"
    );

    private final By emailInput = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[4]/div[2]/div/input"
    );

    private final By passwordInput = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[4]/div[3]/div/input"
    );

    private final By consentControls = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/label[1]"
    );

    private final By submitBtn = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[4]/button"
    );

    private final By userAvatar = By.xpath(
        "/html/body/div[1]/div[2]/header/div[1]/div[1]/div/noindex/div"
    );

    private final By authModal = By.xpath(
        "/html/body/div[1]/div[2]/div[1]"
    );

    private final By cookieAgreeBtn = By.xpath(
        "/html/body/div[1]/div[2]/div[3]/button"
    );

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void login(String email, String password) {
        acceptCookies();
        clickByJs(waitPresent(loginBtn));
        waitVisible(emailInput).sendKeys(email);
        waitPasswordInput().sendKeys(password);
        acceptPersonalDataConsents();
        clickByJs(waitPresent(submitBtn));
        waitLoginFinished();
    }

    public boolean isLoggedIn() {
        return isPresent(userAvatar);
    }

    private WebElement waitPasswordInput() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        } catch (TimeoutException ignored) {
            clickByJs(waitPresent(submitBtn));
            acceptPersonalDataConsents();
            return waitVisible(passwordInput);
        }
    }

    private void acceptPersonalDataConsents() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(consentControls));
            for (WebElement control : driver.findElements(consentControls)) {
                if (control.isDisplayed() && control.isEnabled()) {
                    clickByJs(control);
                }
            }
        } catch (TimeoutException ignored) {
            // Consent checkboxes are not shown every time.
        }
    }

    private void acceptCookies() {
        try {
            WebElement button = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(cookieAgreeBtn));
            if (button.isDisplayed() && button.isEnabled()) {
                clickByJs(button);
            }
        } catch (TimeoutException ignored) {
        }
    }

    private void clickByJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void waitLoginFinished() {
        try {
            waitVisible(userAvatar);
        } catch (TimeoutException ignored) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(authModal));
        }
    }
}
