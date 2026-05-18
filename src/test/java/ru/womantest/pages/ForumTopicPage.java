package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ForumTopicPage extends BasePage {

    private final By messages = By.xpath(
        "//div[contains(@class,'message') or contains(@class,'post') or contains(@class,'reply')]"
        + "[not(contains(@class,'form') or contains(@class,'input'))]"
    );

    private final By replyTextarea = By.xpath(
        "//textarea[contains(@class,'reply') or contains(@class,'message')"
        + " or contains(@placeholder,'сообщение') or contains(@placeholder,'ответ')]"
        + " | //div[@contenteditable='true'][contains(@class,'editor')]"
    );

    private final By submitReplyBtn = By.xpath(
        "//button[contains(text(),'Отправить') or contains(text(),'Ответить')"
        + " or contains(@class,'submit') or contains(@class,'send')][@type='submit']"
    );

    private final By firstAuthorName = By.xpath(
        "(//div[contains(@class,'author') or contains(@class,'user-name')"
        + " or contains(@class,'username')])[1]"
    );

    public ForumTopicPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public List<WebElement> getMessages() {
        return findAll(messages);
    }

    public boolean hasMessages() {
        return isPresent(messages);
    }

    public boolean hasReplyForm() {
        return isPresent(replyTextarea);
    }

    public void typeReply(String text) {
        WebElement textarea = waitVisible(replyTextarea);
        textarea.clear();
        textarea.sendKeys(text);
    }

    public void submitReply() {
        waitClickable(submitReplyBtn).click();
    }

    public void postReply(String text) {
        typeReply(text);
        submitReply();
    }

    public String getFirstAuthorName() {
        return waitVisible(firstAuthorName).getText().trim();
    }

    public int getMessageCount() {
        return driver.findElements(messages).size();
    }
}
