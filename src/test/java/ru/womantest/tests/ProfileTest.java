package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProfileTest — Личный кабинет")
class ProfileTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-18: Раздел «Достижения» открывается")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc18_favoriteAppearsInFavoritesSection(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();
        profile.goToAchievements();

        assertTrue(profile.getCurrentUrl().contains("achievements"));
    }

    @ParameterizedTest(name = "[{0}] TC-19: Раздел «Темы» содержит посты пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc19_myPostsHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();
        profile.goToMyPosts();

        assertTrue(profile.getCurrentUrl().contains("threads"));
    }

    @ParameterizedTest(name = "[{0}] TC-20: Раздел «Ответы» содержит ответы пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc20_myRepliesHasContent(String browser) {
        initDriverAndLogin(browser);
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();
        profile.goToMyReplies();

        assertTrue(profile.getCurrentUrl().contains("messages"));
    }
}
