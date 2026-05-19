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
    private final By myProfileLink = By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/a");
    private final By myPostsLink = By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]/div[3]/a[1]");
    private final By myRepliesLink = By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]/div[3]/a[2]");
    private final By achievementsLink = By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]/div[4]/a[3]");
    private final By topicsTab = myPostsLink;
    private final By allTab = By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]/div[4]/a[1]");
    private final By topicTitles = By.xpath("/html/body/div[3]/div[2]/div[1]/div[1]/div[5]/div[1]/a");
    private final By moderationBadge = By.xpath("./div[1]/span");

    public ProfilePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void openProfileDirect() {
        openProfilePath("");
    }

    public void goToMyProfile() {
        wait.until(ExpectedConditions.elementToBeClickable(myProfileLink)).click();
        wait.until(ExpectedConditions.urlToBe(PROFILE_URL));
    }

    public void goToAchievements() {
        openProfilePath("achievements/");
        wait.until(ExpectedConditions.urlToBe(PROFILE_URL + "achievements/"));
    }

    public void goToMyPosts() {
        openProfilePath("threads/");
        wait.until(ExpectedConditions.urlToBe(PROFILE_URL + "threads/"));
    }

    public void goToMyReplies() {
        openProfilePath("messages/");
        wait.until(ExpectedConditions.urlToBe(PROFILE_URL + "messages/"));
    }

    public void goToTopicsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(topicsTab)).click();
        wait.until(ExpectedConditions.urlToBe(PROFILE_URL + "threads/"));
    }

    public void goToAllTab() {
        wait.until(ExpectedConditions.elementToBeClickable(allTab)).click();
        wait.until(ExpectedConditions.urlToBe(PROFILE_URL + "threads/all/"));
    }

    public boolean hasTopicWithTitle(String title) {
        List<WebElement> topics = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(topicTitles));
        return topics.stream().anyMatch(t -> t.getText().equals(title));
    }

    public boolean topicHasModerationBadge(String title) {
        List<WebElement> topics = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(topicTitles));
        for (WebElement topic : topics) {
            if (topic.getText().equals(title)) {
                WebElement parent = topic.findElement(By.xpath("./.."));
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
