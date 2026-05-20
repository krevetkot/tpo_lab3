package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ArticlePage extends BasePage {

    private final By title = By.xpath("/html/body/div[1]/div[2]/main/div[1]/div[1]/div[1]/div[2]/div[1]/article[1]/div[1]/div[2]/header[1]/div[1]/h1[1]");

    private final By body = By.xpath("/html/body/div[1]/div[2]/main/div[1]/div[1]/div[1]/div[2]/div[1]/article[1]/div[1]/div[2]/section[1]");

    private final By commentsSection = By.xpath(
        "/html/body/div[1]/div[2]/main/div/div/div[1]/div/div/article/div/div/header/div/div[2]/div/span[2]"
    );

    private final By tags = By.xpath(
        "/html/body/div[1]/div[2]/main/div/div/div[1]/div/div/article/div/div/div[2]/a[1]"
    );

    private final By breadcrumbs = By.xpath(
        "/html/body/div[1]/div[2]/main/div/div/div[1]/div/div/article/div/div/nav/a[1]"
    );

    private final By relatedLinks = By.xpath(
        "/html/body/div[1]/div[2]/main/div[1]/div[1]/div[1]/div[2]/div[1]/aside[1]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/a[1]"
    );

    private final By favoriteBtn = By.xpath(
        "/html/body/div[1]/div[2]/main/div/div/div[1]/div/div/article/div/div/header/div/div[2]/button"
    );

    public ArticlePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public String getTitle() {
        return waitVisible(title).getText().trim();
    }

    public boolean hasBody() {
        return isPresent(body) && !driver.findElement(body).getText().isBlank();
    }

    public boolean hasComments() {
        return isPresent(commentsSection);
    }

    public List<WebElement> getTags() {
        return driver.findElements(tags);
    }

    public boolean hasTags() {
        return !getTags().isEmpty();
    }

    public String clickFirstTagAndGetUrl() {
        String oldUrl = driver.getCurrentUrl();
        getTags().get(0).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
        return driver.getCurrentUrl();
    }

    public List<WebElement> getBreadcrumbs() {
        return driver.findElements(breadcrumbs);
    }

    public boolean hasBreadcrumbs() {
        return !getBreadcrumbs().isEmpty();
    }

    public String clickBreadcrumb(int index) {
        String oldUrl = driver.getCurrentUrl();
        getBreadcrumbs().get(index).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
        return driver.getCurrentUrl();
    }

    public boolean hasRelatedLinks() {
        return isPresent(relatedLinks);
    }

    public int getRelatedLinkCount() {
        return driver.findElements(relatedLinks).size();
    }

    public String clickFirstRelatedLink() {
        String oldUrl = driver.getCurrentUrl();
        waitClickable(relatedLinks).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
        return driver.getCurrentUrl();
    }

    public boolean hasFavoriteButton() {
        return isPresent(favoriteBtn);
    }

    public void clickFavorite() {
        waitClickable(favoriteBtn).click();
    }

    public String getFavoriteButtonClass() {
        return driver.findElement(favoriteBtn).getAttribute("class");
    }
}
