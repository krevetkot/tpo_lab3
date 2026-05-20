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
            "/html/body/div[3]/header/div/div[1]/div/div/div[2]"
    );

    private final By authModal = By.xpath(
            "/html/body/div[5]"
    );

    private final By registrationSwitch = By.xpath(
        "/html/body/div[5]/form/div/div[2]/div[5]/div[2]"
    );

    private final By emailInput = By.xpath(
        "/html/body/div[5]/form/div/div[2]/div[4]/div[1]/input"
    );

    private final By nameInput = By.xpath(
        "/html/body/div[5]/form/div/div[2]/div[4]/div[2]/input"
    );

    private final By consentControls = By.xpath(
        "/html/body/div[5]/form/div/div[2]/div[1]//label"
    );

    private final By submitBtn = By.xpath(
        "/html/body/div[5]/form/div/div[2]/div[6]/div[1]/button"
    );

    private final By cookieAgreeBtn = By.xpath(
        "/html/body/div[1]/div[2]/div[3]/button"
    );

    private final By emailConfirmationMessage = By.xpath(
        "/html/body/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/p"
    );

    private final By existingEmailError = By.xpath(
            "/html/body/div[5]/form/div/div[2]/div[4]/div[1]/div/div"
    );

    private final By existingNicknameError = By.xpath(
            "/html/body/div[5]/form/div/div[2]/div[4]/div[2]/div/div"
    );

    private final By forumModal = By.xpath(
            "/html/body/div[5]/form/div/div[2]"
    );

    private final By anonymousBlock = By.xpath(
            "/html/body/div[3]/header/div/div[1]/div/div/div[1]/div[2]/div[2]"
    );


    public RegistrationPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void register(String email, String password, String nickname) {
        acceptCookies();
        openRegistrationForm();
        fillIfPresent(emailInput, email);
        fillIfPresent(nameInput, nickname);
        acceptPersonalDataConsents();
        clickSubmitIfPresent();
        waitRegistrationFinished();
    }

    public boolean isEmailConfirmationStepVisible() {
        return !driver.findElements(emailConfirmationMessage).isEmpty()
            && driver.findElement(emailConfirmationMessage).isDisplayed();
    }

    private void openRegistrationForm() {
        clickByJs(waitPresent(loginBtn));
        waitVisibleWithConsentRetry(authModal);
        clickFirstVisible(registrationSwitch);
        waitVisibleWithConsentRetry(emailInput);
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

    private void waitRegistrationFinished() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !isPresent(anonymousBlock));
        } catch (TimeoutException ignored) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(forumModal));
        }
    }

    private void clickByJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void tryRegisterWithExistingEmail(String existingEmail, String password, String nickname) {
        acceptCookies();
        openRegistrationForm();
        fillIfPresent(emailInput, existingEmail);
        fillIfPresent(nameInput, nickname);
        acceptPersonalDataConsents();
        clickSubmitIfPresent();
    }

    public void tryRegisterWithExistingNickname(String email, String password, String existingNickname) {
        acceptCookies();
        openRegistrationForm();
        fillIfPresent(emailInput, email);
        fillIfPresent(nameInput, existingNickname);
        acceptPersonalDataConsents();
        clickSubmitIfPresent();
    }

    public boolean hasExistingEmailError() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(existingEmailError));
            return true;
        } catch (TimeoutException e) {
            dismissConsentIfPresent();
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(existingEmailError));
            return false;
        }
    }

    public boolean hasExistingNicknameError() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(existingNicknameError));
            return true;
        } catch (TimeoutException e) {
            dismissConsentIfPresent();
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(existingNicknameError));
            return false;
        }
    }

    public boolean isLoggedIn() {
        return !isPresent(anonymousBlock);
    }

}
