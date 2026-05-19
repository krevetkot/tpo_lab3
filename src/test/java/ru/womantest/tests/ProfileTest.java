package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ProfilePage;
import ru.womantest.pages.ArticlePage;
import ru.womantest.pages.MainPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProfileTest — Личный кабинет")
class ProfileTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-21: Добавленная статья отображается в «Избранном»")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc21_favoriteAppearsInFavoritesSection(String browser) {
        initDriverAndLogin(browser);
        ArticlePage article = new MainPage(driver(), getWait()).openFirstArticle();

        article.clickFavorite();

        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();
        profile.goToFavorites();

        assertTrue(profile.getCurrentUrl().contains("favorites"));
    }

    @ParameterizedTest(name = "[{0}] TC-22: Раздел «Темы» содержит посты пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc22_myPostsHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();
        profile.goToMyPosts();

        assertTrue(profile.getCurrentUrl().contains("threads"));
    }

    @ParameterizedTest(name = "[{0}] TC-23: Раздел «Ответы» содержит ответы пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc23_myRepliesHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();
        profile.goToMyReplies();

        assertTrue(profile.getCurrentUrl().contains("messages"));
    }
}