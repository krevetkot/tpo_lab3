package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CreateTopicPage extends BasePage {

    private final By titleInput = By.xpath(
        "//input[@name='title' or @name='subject' or @placeholder[contains(.,'аголовок')"
        + " or contains(.,'ема')]]"
    );

    private final By bodyTextarea = By.xpath(
        "//textarea[@name='body' or @name='text' or @name='message'"
        + " or contains(@placeholder,'текст') or contains(@placeholder,'ообщение')]"
        + " | //div[@contenteditable='true']"
    );

    private final By submitBtn = By.xpath(
        "//button[@type='submit' or contains(text(),'Создать') or contains(text(),'Опубликовать')"
        + " or contains(text(),'Добавить')]"
    );

    private final By validationError = By.xpath(
        "//*[contains(@class,'error') or contains(@class,'invalid')"
        + " or contains(@class,'validation')]"
        + "[not(self::input) and not(self::textarea)]"
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
        By contentEditable = By.xpath("//div[@contenteditable='true']");
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
    }

    public ForumTopicPage submitAndExpectSuccess() {
        submit();
        return new ForumTopicPage(driver, wait);
    }

    public void submitAndExpectError() {
        submit();
    }

    public boolean hasValidationError() {
        return isPresent(validationError);
    }

    public boolean isSubmitDisabled() {
        return "true".equals(driver.findElement(submitBtn).getAttribute("disabled"));
    }
}
