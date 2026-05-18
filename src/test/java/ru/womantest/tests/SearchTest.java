package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.MainPage;
import ru.womantest.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchTest - поиск статей")
class SearchTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-05: поиск Красота возвращает статьи")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc05_searchReturnsResults(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("красота");
        Assumptions.assumeFalse(results.hasCaptcha(), "Поиск заблокирован капчей");

        assertTrue(results.getResultCount() > 0,
            "Поиск по запросу Красота не вернул результатов");
    }

    @ParameterizedTest(name = "[{0}] TC-06: запрос Здоровье отражается в URL")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc06_queryAppearsInUrl(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("здоровье");
        Assumptions.assumeFalse(results.hasCaptcha(), "Поиск заблокирован капчей");
        String url = results.getCurrentUrl().toLowerCase();

        assertTrue(
            url.contains("здоровье") || url.contains("%d0%b7%d0%b4%d0%be%d1%80%d0%be%d0%b2%d1%8c%d0%b5"),
            "URL не содержит поисковый запрос: " + url
        );
    }

    @ParameterizedTest(name = "[{0}] TC-07: бессмысленный запрос не возвращает результатов")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc07_nonsenseQueryShowsNoResults(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("xzxzxzxzxz");
        Assumptions.assumeFalse(results.hasCaptcha(), "Поиск заблокирован капчей");

        assertTrue(
            results.getResultCount() == 0 || results.hasNoResultsMessage(),
            "Бессмысленный запрос вернул результаты"
        );
    }
}
