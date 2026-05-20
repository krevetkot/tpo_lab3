package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CreateTopicPage extends BasePage {

    private final By titleInput = By.xpath(
            "/html/body/div[3]/div[2]/div[1]/form/div[1]/input"
    );

    private final By bodyTextarea = By.xpath(
            "/html/body/div[3]/div[2]/div[1]/form/div[2]/textarea"
    );

    private final By submitBtn = By.xpath(
            "/html/body/div[3]/div[2]/div[1]/form/div[3]/button"
    );

    private final By validationError = By.xpath(
            "/html/body/div[3]/div[2]/div[1]/form/div[1]/div"
    );

    private final By firstModalBtn = By.xpath(
            "/html/body/div[5]/form[2]/div/div[2]/div[6]/div/button"
    );

    private final By secondModalBtn = By.xpath(
            "/html/body/div[5]/section/div/div/div[3]/button"
    );

    public CreateTopicPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isFormLoaded() {
        return isPresent(titleInput) || isPresent(bodyTextarea);
    }

    public void fillTitle(String title) {
        WebElement el = waitVisible(titleInput);
        el.clear();
        el.sendKeys(title);
    }

    public void fillBody(String body) {
        By contentEditable = By.xpath("/html/body/div[3]/div[2]/div[1]/form/div[2]/div");
        if (isPresent(contentEditable)) {
            WebElement el = waitVisible(contentEditable);
            el.click();
            ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1]", el, body);
        } else {
            WebElement el = waitVisible(bodyTextarea);
            el.clear();
            el.sendKeys(body);
        }
    }

    public void submit() {
        waitClickable(submitBtn).click();
        dismissPostSubmitModals();
    }

    public ForumTopicPage submitAndExpectSuccess() {
        submit();
        return new ForumTopicPage(driver, wait);
    }

    public void submitAndExpectError() {
        waitClickable(submitBtn).click();
    }

    public boolean hasValidationError() {
        return isPresent(validationError);
    }

    public boolean isSubmitDisabled() {
        return "true".equals(driver.findElement(submitBtn).getAttribute("disabled"));
    }

    private void dismissPostSubmitModals() {
        dismissModalIfPresent(firstModalBtn);
        dismissModalIfPresent(secondModalBtn);
    }

    private void dismissModalIfPresent(By btnLocator) {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(7))
                    .until(ExpectedConditions.elementToBeClickable(btnLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (TimeoutException ignored) {
        }
    }
}