package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegistrationPage extends BasePage {

    private final By loginBtn = By.xpath(
        "/html/body/div[1]/div[2]/header/div[1]/div[1]/div/noindex/div/button"
    );

    private final By authModal = By.xpath(
        "/html/body/div[1]/div[2]/div[1]"
    );

    private final By registrationSwitch = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/a"
    );

    private final By emailInput = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div/input"
    );

    private final By emailRegistrationMethod = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/button[1]"
    );

    private final By passwordInput = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[2]/div/input"
    );

    private final By nameInput = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[3]/div/input"
    );

    private final By consentControls = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/label[1]"
    );

    private final By submitBtn = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/button[1]"
    );

    private final By cookieAgreeBtn = By.xpath(
        "/html/body/div[1]/div[2]/div[3]/button"
    );

    private final By emailConfirmationMessage = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/p"
    );

    public RegistrationPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void registerUntilEmailConfirmation(String email, String password, String nickname) {
        acceptCookies();
        openRegistrationForm();
        fillIfPresent(emailInput, email);
        clickSubmitIfPresent();
        fillIfPresent(nameInput, nickname);
        fillIfPresent(passwordInput, password);
        acceptPersonalDataConsents();
        clickSubmitIfPresent();
        waitForEmailConfirmationStep();
    }

    public boolean isEmailConfirmationStepVisible() {
        return !driver.findElements(emailConfirmationMessage).isEmpty()
            && driver.findElement(emailConfirmationMessage).isDisplayed();
    }

    private void openRegistrationForm() {
        clickByJs(waitPresent(loginBtn));
        waitVisible(authModal);
        clickFirstVisible(registrationSwitch);
        clickFirstVisible(emailRegistrationMethod);
        waitVisible(emailInput);
    }

    private void clickFirstVisible(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            if (element.isDisplayed() && element.isEnabled()) {
                clickByJs(element);
                return;
            }
        }
        clickByJs(waitPresent(locator));
    }

    private void fillIfPresent(By locator, String value) {
        try {
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.clear();
            element.sendKeys(value);
        } catch (TimeoutException ignored) {
        }
    }

    private void clickSubmitIfPresent() {
        try {
            WebElement button = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(submitBtn));
            if (button.isDisplayed() && button.isEnabled()) {
                clickByJs(button);
            }
        } catch (TimeoutException ignored) {
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

    private void waitForEmailConfirmationStep() {
        waitVisible(emailConfirmationMessage);
    }

    private void clickByJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
