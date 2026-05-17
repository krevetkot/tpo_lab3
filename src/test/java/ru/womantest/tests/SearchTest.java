package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.MainPage;
import ru.womantest.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchTest — Поиск статей")
class SearchTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-05: Поиск «красота» возвращает статьи")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc05_searchReturnsResults(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("красота");

        assertTrue(results.getResultCount() > 0,
            "Поиск по «красота» не вернул результатов");
    }

    @ParameterizedTest(name = "[{0}] TC-06: Запрос «здоровье» отражается в URL")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc06_queryAppearsInUrl(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("здоровье");
        String url = results.getCurrentUrl().toLowerCase();

        assertTrue(
            url.contains("здоровье") || url.contains("%D0%B7%D0%B4%D0%BE%D1%80%D0%BE%D0%B2%D1%8C%D0%B5"),
            "URL не содержит поисковый запрос: " + url
        );
    }

    @ParameterizedTest(name = "[{0}] TC-07: Бессмысленный запрос — нет результатов")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc07_nonsenseQueryShowsNoResults(String browser) {
        initDriver(browser);
        SearchResultsPage results = new MainPage(driver(), getWait()).search("xzxzxzxzxz");

        assertTrue(
            results.getResultCount() == 0 || results.hasNoResultsMessage(),
            "Бессмысленный запрос вернул результаты — возможно, поиск нечёткий"
        );
    }
}
