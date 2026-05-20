package ru.womantest.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProfilePage extends BasePage {

    private static final String PROFILE_URL = "https://www.woman.ru/user/ksenia12345678-id403945603/";

    private final By profileIconBtn = By.xpath(
            "/html/body/div[3]/header/div/div[1]/div/div/div/a/div"
    );

    private final By myProfileLink = By.xpath(
            "/html/body/div[3]/header/div/div[1]/div/div/div/div/div[1]/a"
    );

    private final By allTab = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[2]/a[1]"
    );

    private final By topicsTab = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[2]/a[1]"
    );

    private final By favoritesTab = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[2]/a[3]"
    );

    private final By tabContent = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[3]"
    );

    private final By myPostsLink = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[1]/a[1]"
    );

    private final By myRepliesLink = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[1]/a[2]"
    );

    private final By achievementsLink = By.xpath(
            "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/a[3]"
    );

    private final By profileRoot = By.xpath(
            "/html/body/div[3]/div[4]"
    );

    private final By topicTitles = By.xpath("/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[3]/div[1]/h2");
    private final By moderationBadge = By.xpath("/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[3]/div[1]/h2/span");
    private final By topicCount = By.xpath("/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[1]/a[1]/div[1]");

    public ProfilePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void openViaIcon() {
        waitClickable(profileIconBtn).click();
    }

    public void goToMyProfile() {
        waitClickable(myProfileLink).click();
    }

    public void goToAllTab() {
        waitClickable(allTab).click();
    }

    public void goToFavoritesTab() {
        waitClickable(favoritesTab).click();
    }

    public boolean isTabContentNotEmpty() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(tabContent));
            return !driver.findElements(tabContent).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean hasTopics () {
        String text = driver.findElement(By.xpath(
                "/html/body/div[3]/div[4]/div/div[2]/div[2]/div[2]/div[1]/a[1]/div[1]"
        )).getText().trim();
        return Integer.parseInt(text) > 0;
    }

    public void openProfileDirect() {
        openProfilePath("");
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
        waitClickable(topicsTab).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    private void openProfilePath(String path) {
        try {
            driver.get(PROFILE_URL + path);
        } catch (org.openqa.selenium.TimeoutException ignored) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.stop();");
        }
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(profileRoot),
                ExpectedConditions.visibilityOfElementLocated(myPostsLink),
                ExpectedConditions.visibilityOfElementLocated(myRepliesLink),
                ExpectedConditions.visibilityOfElementLocated(achievementsLink)
        ));
    }

    public boolean hasTopicWithTitle(String title) {
        java.util.List<org.openqa.selenium.WebElement> topics = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(topicTitles));
        return topics.stream().anyMatch(t -> t.getText().contains(title));
    }

    public boolean topicHasModerationBadge(String title) {
        java.util.List<org.openqa.selenium.WebElement> topics = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(topicTitles));
        for (org.openqa.selenium.WebElement topic : topics) {
            if (topic.getText().contains(title)) {
                org.openqa.selenium.WebElement parent = topic.findElement(By.xpath("./.."));
                return !parent.findElements(moderationBadge).isEmpty();
            }
        }
        return false;
    }
}
