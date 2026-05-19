package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResultsPage extends BasePage {

    private final By resultItems = By.xpath(
        "/html/body/div[3]/div[2]/div[1]/div[2]/div[1]/div/h2/a"
    );

    private final By noResultsMsg = By.xpath(
        "/html/body/div[3]/div[2]/div[1]/div[2]"
    );

    public SearchResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public int getResultCount() {
        if (!isPresent(resultItems)) return 0;
        return driver.findElements(resultItems).size();
    }

    public boolean hasNoResultsMessage() {
        return isPresent(noResultsMsg);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
