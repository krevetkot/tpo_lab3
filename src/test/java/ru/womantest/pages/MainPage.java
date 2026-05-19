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
        "//a[contains(@class,'logo')] | //header//img[contains(@alt,'woman') or contains(@alt,'Woman')]"
    );

    private final By navItems = By.xpath(
        "//nav//a[not(contains(@class,'logo'))]"
        + " | //ul[contains(@class,'menu') or contains(@class,'nav')]//a"
        + " | //header//a[contains(@href,'/beauty/') or contains(@href,'/health/') or contains(@href,'/relations/')]"
    );

    private final By searchToggle = By.xpath(
        "//button[contains(@class,'search') or @aria-label='Поиск']"
        + " | //a[contains(@class,'search') or @aria-label='Поиск']"
    );

    private final By searchInput = By.xpath(
        "//input[@type='search' or @name='q'"
        + " or contains(@placeholder,'поиск') or contains(@placeholder,'Поиск')"
        + " or contains(@placeholder,'ищите') or contains(@placeholder,'Ищите')]"
    );

    private final By firstArticleLink = By.xpath(
        "(//article//a[@href] | //div[contains(@class,'article')]//a[@href]"
        + " | //div[contains(@class,'item')]//a[@href])[1]"
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
        By byHref = By.xpath(
            "//a[contains(@class,'header__nav-link') and contains(@href,'" + hrefPath + "')]"
            + " | //nav//a[contains(@href,'" + hrefPath + "') and not(contains(@href,'.svg'))]"
            + " | //header//a[contains(@href,'" + hrefPath + "') and not(contains(@href,'.svg')) and not(contains(@href,'#'))]"
        );
        WebElement link = waitPresent(byHref);
        String href = link.getAttribute("href");
        if (href != null && !href.isBlank()) {
            driver.get(href);
        } else {
            link.click();
        }
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
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        return new ArticlePage(driver, wait);
    }
}
