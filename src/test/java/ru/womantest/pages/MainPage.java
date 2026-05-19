package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class MainPage extends BasePage {

    private static final String BASE_URL = "https://www.woman.ru";

    private final By logo = By.xpath(
        "/html/body/div[1]/div[2]/header/div[1]/span"
    );

    private final By navItems = By.xpath(
        "/html/body/div[1]/div[2]/header/div[2]/div[1]/div[1]/div[1]/nav/ul/li[1]/a"
    );

    private final By searchToggle = By.xpath(
        "/html/body/div[1]/div[2]/header/div[1]/div[1]/div/button"
    );

    private final By searchInput = By.xpath(
        "/html/body/div[1]/div[2]/div[2]/div/div/div/input"
    );

    private final By firstArticleLink = By.xpath(
        "/html/body/div[1]/div[2]/main/div/div/div[1]/div[1]/div[1]/div[2]/span/a"
    );

    public MainPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public boolean isLoaded() {
        return isPresent(logo) || !driver.getTitle().isEmpty();
    }

    public List<WebElement> getNavItems() {
        return findAll(navItems);
    }

    public void clickNavItem(int index) {
        getNavItems().get(index).click();
    }

    public String getNavItemText(int index) {
        return getNavItems().get(index).getText().trim();
    }

    public void clickNavSection(String hrefPath) {
        driver.get(BASE_URL + hrefPath);
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(BASE_URL + "/")));
    }

    public void openSearch() {
        if (isPresent(searchToggle)) {
            waitClickable(searchToggle).click();
        }
    }

    public SearchResultsPage search(String query) {
        openSearch();
        if (!isPresent(searchInput)) {
            openSearchUrl(query);
            return new SearchResultsPage(driver, wait);
        }

        String oldUrl = driver.getCurrentUrl();
        WebElement input = waitVisible(searchInput);
        input.clear();
        input.sendKeys(query + Keys.ENTER);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
        } catch (TimeoutException ignored) {
            openSearchUrl(query);
        }
        return new SearchResultsPage(driver, wait);
    }

    private void openSearchUrl(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        driver.get(BASE_URL + "/search/?q=" + encodedQuery + "&where=forum");
    }

    public ArticlePage openFirstArticle() {
        WebElement link = waitPresent(firstArticleLink);
        String href = link.getAttribute("href");
        if (href != null && !href.isBlank()) {
            driver.get(href);
        } else {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
        return new ArticlePage(driver, wait);
    }
}
