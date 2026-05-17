package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class MainPage extends BasePage {

    private final By logo = By.xpath(
        "//a[contains(@class,'logo')] | //header//img[contains(@alt,'woman') or contains(@alt,'Woman')]"
    );

    private final By navItems = By.xpath(
        "//nav//a[not(contains(@class,'logo'))]"
        + " | //ul[contains(@class,'menu') or contains(@class,'nav')]//a"
    );

    private final By searchToggle = By.xpath(
        "//button[contains(@class,'search') or @aria-label='Поиск']"
        + " | //a[contains(@class,'search') or @aria-label='Поиск']"
    );

    private final By searchInput = By.xpath(
        "//input[@type='search' or @name='q'"
        + " or @placeholder[contains(.,'оиск') or contains(.,'ищите')]]"
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

    public void clickNavItemByText(String text) {
        By byText = By.xpath(
            "//nav//a[normalize-space()='" + text + "']"
            + " | //ul[contains(@class,'menu')]//a[normalize-space()='" + text + "']"
        );
        waitClickable(byText).click();
    }

    public void openSearch() {
        if (isPresent(searchToggle)) {
            waitClickable(searchToggle).click();
        }
    }

    public SearchResultsPage search(String query) {
        openSearch();
        WebElement input = waitVisible(searchInput);
        input.clear();
        input.sendKeys(query + Keys.ENTER);
        return new SearchResultsPage(driver, wait);
    }

    public ArticlePage openFirstArticle() {
        waitClickable(firstArticleLink).click();
        return new ArticlePage(driver, wait);
    }
}
