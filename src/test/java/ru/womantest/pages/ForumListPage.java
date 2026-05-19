package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ForumListPage extends BasePage {

    private final By forumNavLink = By.xpath(
        "/html/body/div[1]/div[2]/header/div[2]/div[1]/div[1]/div[1]/nav/ul/li[1]/a"
    );

    private final By topicLinks = By.xpath(
        "/html/body/div[3]/div[3]/div[1]/div[3]/div[2]/div[1]/ul/li[1]/a"
    );

    private final By createTopicBtn = By.xpath(
        "/html/body/div[3]/div[2]/div[2]/button"
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
        WebElement link = waitClickable(topicLinks);
        String href = link.getAttribute("href");
        if (href != null && !href.isBlank()) {
            driver.get(href);
        } else {
            link.click();
        }
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
