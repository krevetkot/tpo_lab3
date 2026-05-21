package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ForumTopicPage extends BasePage {

    private final By messages = By.xpath(
        "/html/body/div[3]/div[4]/div[1]/div[2]"
    );

    private final By replyTextarea = By.xpath(
        "/html/body/div[3]/div[2]/div[1]/div[1]/form/div[2]/textarea"
    );

    private final By submitReplyBtn = By.xpath(
        "/html/body/div[3]/div[2]/div[1]/div[1]/form/div[3]/button"
    );

    private final By firstAuthorName = By.xpath(
        "/html/body/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]"
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

    public int getMessageCount() {
        return driver.findElements(messages).size();
    }
}
