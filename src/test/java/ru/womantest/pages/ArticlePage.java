package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ArticlePage extends BasePage {

    private final By title = By.xpath("//h1");

    private final By body = By.xpath(
        "//div[contains(@class,'article-body') or contains(@class,'article__body')"
        + " or contains(@class,'content') or contains(@class,'text')]//p[1]"
    );

    private final By commentsSection = By.xpath(
        "//div[contains(@class,'comment') or contains(@id,'comment')]"
        + " | //section[contains(@class,'comment')]"
    );

    private final By tags = By.xpath(
        "//a[contains(@class,'tag') or contains(@class,'label')]"
        + "[ancestor::div[contains(@class,'tag') or contains(@class,'label') or contains(@class,'topic')]]"
    );

    private final By breadcrumbs = By.xpath(
        "//nav[contains(@aria-label,'breadcrumb') or contains(@class,'breadcrumb')]//a"
        + " | //ol[contains(@class,'breadcrumb')]//a"
        + " | //div[contains(@class,'breadcrumb')]//a"
    );

    private final By relatedLinks = By.xpath(
        "//a[contains(@class,'announce-inline-tile')][@href]"
    );

    private final By favoriteBtn = By.xpath(
        "//button[contains(@class,'favorite') or contains(@class,'bookmark')"
        + " or contains(@aria-label,'избранное') or contains(@aria-label,'Избранное'" +
                ")]"
        + " | //a[contains(@class,'favorite') or contains(@class,'bookmark')]"
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
