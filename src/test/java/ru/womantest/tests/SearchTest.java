package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.MainPage;
import ru.womantest.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchTest - поиск по форуму")
class SearchTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-05: поиск Диета возвращает темы форума")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc05_searchReturnsResults(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("диета");

        assertTrue(results.getResultCount() > 0,
            "Поиск по запросу Диета не вернул результатов");
    }

    @ParameterizedTest(name = "[{0}] TC-06: запрос Здоровье отражается в URL")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc06_queryAppearsInUrl(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("здоровье");
        String url = results.getCurrentUrl().toLowerCase();

        assertTrue(url.contains("/search/") && url.contains("q="),
            "URL не содержит страницу поиска и параметр q: " + url);
    }

    @ParameterizedTest(name = "[{0}] TC-07: бессмысленный запрос не возвращает результатов")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc07_nonsenseQueryShowsNoResults(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("xzxzxzxzxz");

        assertTrue(
            results.getResultCount() == 0 || results.hasNoResultsMessage(),
            "Бессмысленный запрос вернул результаты"
        );
    }
}
