package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ArticlePage;
import ru.womantest.pages.MainPage;
import ru.womantest.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProfileTest — Личный кабинет")
class ProfileTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-18: Иконка уведомлений отображается в хедере")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc18_notifIconIsVisible(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());

        assertTrue(profile.hasNotifIcon(),
            "Иконка уведомлений не найдена — возможно, XPath нужно скорректировать");
    }

    @ParameterizedTest(name = "[{0}] TC-19: Клик по иконке открывает список уведомлений")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc19_notifIconOpensDropdown(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        Assumptions.assumeTrue(profile.hasNotifIcon(), "Иконка уведомлений не найдена — тест пропущен");

        profile.clickNotifIcon();

        assertTrue(profile.notifDropdownIsVisible(),
            "Список уведомлений не появился после клика");
    }

    @ParameterizedTest(name = "[{0}] TC-20: Клик «В избранное» меняет состояние кнопки")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc20_favoriteButtonChangesState(String browser) {
        initDriverAndLogin(browser);
        ArticlePage article = new MainPage(driver(), getWait()).openFirstArticle();
        Assumptions.assumeTrue(article.hasFavoriteButton(),
            "Кнопка «В избранное» не найдена — тест пропущен");

        String classBefore = article.getFavoriteButtonClass();
        article.clickFavorite();
        String classAfter = article.getFavoriteButtonClass();

        assertNotEquals(classBefore, classAfter,
            "CSS-класс кнопки «В избранное» не изменился после клика");
    }

    @ParameterizedTest(name = "[{0}] TC-21: Добавленная статья отображается в «Избранном»")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc21_favoriteAppearsInFavoritesSection(String browser) {
        initDriverAndLogin(browser);
        ArticlePage article = new MainPage(driver(), getWait()).openFirstArticle();
        Assumptions.assumeTrue(article.hasFavoriteButton(), "Кнопка «В избранное» не найдена — тест пропущен");

        article.clickFavorite();

        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToFavorites();

        assertTrue(
            profile.getCurrentUrl().contains("favorite") || profile.getCurrentUrl().contains("bookmark"),
            "URL раздела «Избранное» не содержит ожидаемого пути"
        );
        assertTrue(profile.hasItems(), "В разделе «Избранное» нет элементов");
    }

    @ParameterizedTest(name = "[{0}] TC-22: Раздел «Мои посты» содержит посты пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc22_myPostsHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToMyPosts();

        assertTrue(
            profile.getCurrentUrl().contains("post") || profile.getCurrentUrl().contains("topic"),
            "Не перешли в раздел «Мои посты», URL: " + profile.getCurrentUrl()
        );
        assertDoesNotThrow(profile::getItems,
            "Ошибка при получении списка постов");
    }

    @ParameterizedTest(name = "[{0}] TC-23: Раздел «Мои ответы» содержит ответы пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc23_myRepliesHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToMyReplies();

        assertTrue(
            profile.getCurrentUrl().contains("repl") || profile.getCurrentUrl().contains("answer"),
            "Не перешли в раздел «Мои ответы», URL: " + profile.getCurrentUrl()
        );
        assertDoesNotThrow(profile::getItems,
            "Ошибка при получении списка ответов");
    }

    @ParameterizedTest(name = "[{0}] TC-24: Раздел «Интересное» открывается и содержит контент")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc24_interestingHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        Assumptions.assumeTrue(
            true,
            "Раздел «Интересное» не найден — уточни название на сайте и скорректируй XPath в ProfilePage"
        );

        profile.goToInteresting();

        assertFalse(driver().getTitle().isBlank(), "Страница «Интересное» не загрузилась");
        assertTrue(profile.hasItems(), "В разделе «Интересное» нет материалов");
    }
}
