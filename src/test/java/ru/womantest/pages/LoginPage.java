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
        + " | //button[contains(@class,'header-auth__button') and normalize-space()='Войти']"
    );

    private final By emailInput = By.xpath(
        "//div[contains(@class,'ds-auth-modal') or contains(@class,'auth-modal')]"
        + "//input[@type='email' or @name='email' or @name='login'"
        + " or contains(@placeholder,'mail') or contains(@placeholder,'Почта')]"
    );

    private final By passwordInput = By.xpath(
        "//div[contains(@class,'ds-auth-modal') or contains(@class,'auth-modal')]"
        + "//input[@type='password' or contains(@placeholder,'Пароль')]"
    );

    private final By consentControls = By.xpath(
        "//div[contains(@class,'ds-auth-modal') or contains(@class,'auth-modal')]//label[contains(normalize-space(.),'Даю согласие на обработку персональных данных')"
        + " or contains(normalize-space(.),'С Политикой обработки персональных данных согласен')]"
        + " | //div[contains(@class,'ds-auth-modal') or contains(@class,'auth-modal')]//button[contains(normalize-space(.),'Даю согласие на обработку персональных данных')"
        + " or contains(normalize-space(.),'С Политикой обработки персональных данных согласен')]"
        + " | //div[contains(@class,'ds-auth-modal') or contains(@class,'auth-modal')]//span[contains(normalize-space(.),'Даю согласие на обработку персональных данных')"
        + " or contains(normalize-space(.),'С Политикой обработки персональных данных согласен')]"
        + "/ancestor::label[1]"
    );

    private final By submitBtn = By.xpath(
        "//div[contains(@class,'ds-auth-modal') or contains(@class,'auth')]//button[not(contains(@class,'header-auth__button'))"
        + " and (@type='submit' or contains(@class,'submit') or normalize-space()='Войти' or normalize-space()='Продолжить')]"
        + " | //form//button[not(contains(@class,'header-auth__button'))"
        + " and (@type='submit' or contains(@class,'submit') or normalize-space()='Войти' or normalize-space()='Продолжить')]"
    );

    private final By userAvatar = By.xpath(
        "//header//a[contains(@class,'avatar') or contains(@class,'profile') or contains(@class,'user')]"
        + " | //header//button[contains(@class,'avatar') or contains(@class,'profile') or contains(@class,'user')]"
        + " | //header//*[contains(@class,'notif') or contains(@class,'notification') or contains(@class,'bell')]"
        + " | //div[contains(@class,'user-name')]"
    );

    private final By authModal = By.xpath(
        "//*[contains(@class,'ds-auth-modal') or contains(@class,'auth-modal')]"
    );

    private final By cookieAgreeBtn = By.xpath(
        "//div[contains(@class,'cookiesPopup')]//button[contains(normalize-space(.),'Согласен')]"
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
