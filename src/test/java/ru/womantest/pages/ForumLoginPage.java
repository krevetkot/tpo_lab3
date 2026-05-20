package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ForumLoginPage extends BasePage {

    private final By signInBtn = By.xpath(
            "/html/body/div[3]/header/div/div[1]/div/div/div[2]"
    );

    private final By forumModal = By.xpath(
            "/html/body/div[5]/form/div/div[2]"
    );

    private final By emailInput = By.xpath(
            "/html/body/div[5]/form/div/div[2]/div[4]/div[1]/input"
    );

    private final By passwordInput = By.xpath(
            "/html/body/div[5]/form/div/div[2]/div[4]/div[2]/input"
    );

    private final By consentControls = By.xpath(
            "//html/body/div[5]/form/div/div[2]/div[1]/div/label"
    );

    private final By submitBtn = By.xpath(
            "/html/body/div[5]/form/div/div[2]/div[5]/div[1]/button"
    );

    private final By anonymousBlock = By.xpath(
            "/html/body/div[3]/header/div/div[1]/div/div/div[1]/div[2]/div[2]"
    );

    private final By loginError = By.xpath(
            "/html/body/div[5]/form/div/div[2]/div[4]/div[2]/div/div"
    );

    public ForumLoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void login(String email, String password) {
        clickByJs(waitPresent(signInBtn));
        waitVisible(emailInput).sendKeys(email);
        waitPasswordInput().sendKeys(password);
        acceptPersonalDataConsents();
        clickByJs(waitPresent(submitBtn));
        waitLoginFinished();
    }

    public void loginAndWaitForResult(String email, String password) {
        clickByJs(waitPresent(signInBtn));
        waitVisible(emailInput).sendKeys(email);
        waitPasswordInput().sendKeys(password);
        acceptPersonalDataConsents();
        clickByJs(waitPresent(submitBtn));
        waitLoginOrError();
    }

    public boolean isLoggedIn() {
        return !isPresent(anonymousBlock);
    }

    public boolean hasLoginError() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(7))
                    .until(d -> {
                        boolean errorVisible = d.findElements(loginError).stream()
                                .anyMatch(WebElement::isDisplayed);
                        boolean modalStillOpen = !d.findElements(forumModal).isEmpty()
                                && d.findElement(forumModal).isDisplayed();
                        return errorVisible || (modalStillOpen);
                    });
            boolean modalOpen = isPresent(forumModal)
                    && driver.findElement(forumModal).isDisplayed();
            return modalOpen && !isLoggedIn();
        } catch (TimeoutException e) {
            return false;
        }
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
//            new WebDriverWait(driver, Duration.ofSeconds(3))
//                    .until(ExpectedConditions.presenceOfElementLocated(consentControls2));
//            for (WebElement control : driver.findElements(consentControls2)) {
//                if (control.isDisplayed() && control.isEnabled()) {
//                    clickByJs(control);
//                }
//            }
        } catch (TimeoutException ignored) {
        }
    }

    private void waitLoginFinished() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> !isPresent(anonymousBlock));
        } catch (TimeoutException ignored) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(forumModal));
        }
    }

    private void waitLoginOrError() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d ->
                            !isPresent(anonymousBlock)
                                    || (isPresent(forumModal) && driver.findElement(forumModal).isDisplayed())
                    );
        } catch (TimeoutException ignored) {
        }
    }

    private void clickByJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}