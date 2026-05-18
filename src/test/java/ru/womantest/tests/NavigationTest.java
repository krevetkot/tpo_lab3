package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.MainPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NavigationTest - навигация по разделам")
@Execution(ExecutionMode.CONCURRENT)
class NavigationTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-01: главная страница содержит навигационное меню")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc01_mainPageHasNavMenu(String browser) {
        initDriver(browser);
        MainPage page = new MainPage(driver(), getWait());

        assertTrue(page.isLoaded(), "Главная страница не загрузилась");
        assertTrue(page.getNavItems().size() > 0, "Навигационное меню пустое");
    }

    @ParameterizedTest(name = "[{0}] TC-02: переход в раздел Красота")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc02_navigateToBeauty(String browser) {
        initDriver(browser);
        MainPage page = new MainPage(driver(), getWait());
        String urlBefore = driver().getCurrentUrl();

        page.clickNavItemByText("Красота");

        assertNotEquals(urlBefore, driver().getCurrentUrl(), "URL не изменился");
        assertFalse(driver().getTitle().isBlank(), "Заголовок страницы пустой");
    }

    @ParameterizedTest(name = "[{0}] TC-03: переход в раздел Здоровье")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc03_navigateToHealth(String browser) {
        initDriver(browser);
        MainPage page = new MainPage(driver(), getWait());
        String urlBefore = driver().getCurrentUrl();

        page.clickNavItemByText("Здоровье");

        assertNotEquals(urlBefore, driver().getCurrentUrl(), "URL не изменился");
        assertFalse(driver().getTitle().isBlank(), "Заголовок страницы пустой");
    }

    @ParameterizedTest(name = "[{0}] TC-04: переход в раздел Отношения")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc04_navigateToRelationships(String browser) {
        initDriver(browser);
        MainPage page = new MainPage(driver(), getWait());
        String urlBefore = driver().getCurrentUrl();

        page.clickNavItemByText("Отношения");

        assertNotEquals(urlBefore, driver().getCurrentUrl(), "URL не изменился");
        assertFalse(driver().getTitle().isBlank(), "Заголовок страницы пустой");
    }
}
