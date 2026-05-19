package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

public class ProfilePage extends BasePage {

    private static final String PROFILE_URL = "https://www.woman.ru/user/ksenia12345678-id403945603/";

    private final By myProfileLink = By.xpath("//a[contains(@class,'user-info__name')]");
    private final By myPostsLink = By.xpath("//a[contains(@href,'threads')]");
    private final By myRepliesLink = By.xpath("//a[contains(@href,'messages')]");
    private final By achievementsLink = By.xpath("//a[contains(@href,'achievements')]");
    private final By topicsTab = By.xpath("//a[contains(@href,'threads')]");
    private final By allTab = By.xpath("//a[contains(@href,'all')]");
    private final By topicTitles = By.xpath("//a[contains(@class,'thread-title')]");
    private final By moderationBadge = By.xpath(".//span[contains(@class,'moderation-badge')]");

    public ProfilePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void openProfileDirect() {
        openProfilePath("");
    }

    public void goToMyProfile() {
        wait.until(ExpectedConditions.elementToBeClickable(myProfileLink)).click();
        wait.until(ExpectedConditions.urlContains("/user/"));
    }

    public void goToAchievements() {
        openProfilePath("achievements/");
        wait.until(ExpectedConditions.urlContains("achievements"));
    }

    public void goToMyPosts() {
        openProfilePath("threads/");
        wait.until(ExpectedConditions.urlContains("threads"));
    }

    public void goToMyReplies() {
        openProfilePath("messages/");
        wait.until(ExpectedConditions.urlContains("messages"));
    }

    public void goToTopicsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(topicsTab)).click();
        wait.until(ExpectedConditions.urlContains("threads"));
    }

    public void goToAllTab() {
        wait.until(ExpectedConditions.elementToBeClickable(allTab)).click();
        wait.until(ExpectedConditions.urlContains("all"));
    }

    public boolean hasTopicWithTitle(String title) {
        List<WebElement> topics = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(topicTitles));
        return topics.stream().anyMatch(t -> t.getText().equals(title));
    }

    public boolean topicHasModerationBadge(String title) {
        List<WebElement> topics = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(topicTitles));
        for (WebElement topic : topics) {
            if (topic.getText().equals(title)) {
                WebElement parent = topic.findElement(By.xpath("./ancestor::div[contains(@class,'thread-container')]"));
                return !parent.findElements(moderationBadge).isEmpty();
            }
        }
        return false;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    private void openProfilePath(String path) {
        try {
            driver.get(PROFILE_URL + path);
        } catch (TimeoutException ignored) {
            ((JavascriptExecutor) driver).executeScript("window.stop();");
        }
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(myPostsLink),
            ExpectedConditions.visibilityOfElementLocated(myRepliesLink),
            ExpectedConditions.visibilityOfElementLocated(achievementsLink)
        ));
    }
}
