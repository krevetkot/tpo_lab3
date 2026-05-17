package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ForumListPage extends BasePage {

    private final By forumNavLink = By.xpath(
        "//a[contains(@href,'forum') or contains(@href,'community')"
        + " or normalize-space()='Форум' or normalize-space()='Форумы']"
    );

    private final By topicLinks = By.xpath(
        "//div[contains(@class,'forum') or contains(@class,'topic')"
        + " or contains(@class,'thread')]//a[@href]"
        + " | //ul[contains(@class,'forum')]//a[@href]"
    );

    private final By createTopicBtn = By.xpath(
        "//a[contains(text(),'Создать тему') or contains(text(),'Новая тема')"
        + " or contains(@class,'create') or contains(@class,'new-topic')]"
        + " | //button[contains(text(),'Создать') or contains(text(),'Новая тема')]"
    );

    public ForumListPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public ForumListPage open() {
        waitClickable(forumNavLink).click();
        return this;
    }

    public List<WebElement> getTopicLinks() {
        return findAll(topicLinks);
    }

    public boolean hasTopics() {
        return isPresent(topicLinks);
    }

    public ForumTopicPage openFirstTopic() {
        waitClickable(topicLinks).click();
        return new ForumTopicPage(driver, wait);
    }

    public CreateTopicPage clickCreateTopic() {
        waitClickable(createTopicBtn).click();
        return new CreateTopicPage(driver, wait);
    }

    public boolean hasCreateTopicButton() {
        return isPresent(createTopicBtn);
    }
}
