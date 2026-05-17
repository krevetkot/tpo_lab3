package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class SearchResultsPage extends BasePage {

    private final By resultItems = By.xpath(
        "//div[contains(@class,'result') or contains(@class,'search')]//a[@href]"
        + " | //ul[contains(@class,'result')]//li//a[@href]"
        + " | //article//a[@href]"
    );

    private final By noResultsMsg = By.xpath(
        "//*[contains(text(),'ничего не найдено') or contains(text(),'нет результатов')"
        + " or contains(text(),'не найден') or contains(text(),'0 результатов')]"
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
