package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ProfilePage extends BasePage {

    private final By notifIcon = By.xpath(
        "//a[contains(@class,'notif') or contains(@class,'bell') or @aria-label='Уведомления']"
        + " | //button[contains(@class,'notif') or contains(@class,'bell')]"
    );

    private final By notifDropdown = By.xpath(
        "//div[contains(@class,'notif') or contains(@class,'notification')]"
        + "[contains(@class,'dropdown') or contains(@class,'popup') or contains(@class,'list')]"
    );

    private final By profileMenuToggle = By.xpath(
        "//a[contains(@class,'avatar') or contains(@class,'profile')]"
        + " | //button[contains(@class,'user') or contains(@class,'profile')]"
    );

    private final By favoritesLink = By.xpath(
        "//a[contains(@href,'favorite') or contains(@href,'bookmark')"
        + " or normalize-space()='Избранное']"
    );

    private final By myPostsLink = By.xpath(
        "//a[contains(@href,'my-post') or contains(@href,'myposts') or contains(@href,'my_post')"
        + " or normalize-space()='Мои посты' or normalize-space()='Мои темы']"
    );

    private final By myRepliesLink = By.xpath(
        "//a[contains(@href,'my-repl') or contains(@href,'myreplies')"
        + " or normalize-space()='Мои ответы']"
    );

    private final By interestingLink = By.xpath(
        "//a[normalize-space()='Интересное' or contains(@href,'interesting')"
        + " or contains(@href,'recommend')]"
    );

    private final By listItems = By.xpath(
        "//article//a[@href] | //div[contains(@class,'item')]//a[@href]"
        + " | //li[contains(@class,'item')]//a[@href]"
    );

    public ProfilePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean hasNotifIcon() {
        return isPresent(notifIcon);
    }

    public void clickNotifIcon() {
        waitClickable(notifIcon).click();
    }

    public boolean notifDropdownIsVisible() {
        return isPresent(notifDropdown);
    }

    private void openProfileMenu() {
        if (isPresent(profileMenuToggle)) {
            waitClickable(profileMenuToggle).click();
        }
    }

    public void goToFavorites() {
        openProfileMenu();
        waitClickable(favoritesLink).click();
    }

    public void goToMyPosts() {
        openProfileMenu();
        waitClickable(myPostsLink).click();
    }

    public void goToMyReplies() {
        openProfileMenu();
        waitClickable(myRepliesLink).click();
    }

    public void goToInteresting() {
        waitClickable(interestingLink).click();
    }

    public boolean hasItems() {
        return isPresent(listItems);
    }

    public List<WebElement> getItems() {
        return driver.findElements(listItems);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
