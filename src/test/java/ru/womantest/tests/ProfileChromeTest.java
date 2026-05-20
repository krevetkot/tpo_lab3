package ru.womantest.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProfileTest Chrome — Профиль на форуме")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileChromeTest extends BaseTest {

    @BeforeAll
    void setUpOnce() {
        setManagedExternally();
        initDriverAndLoginForum("chrome");
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openViaIcon();
        profile.goToMyProfile();
    }

    @AfterAll
    void tearDownOnce() {
        quitDriver();
    }

    @Test
    @DisplayName("TC-30 [chrome]: Вкладка «Все» в профиле непуста")
    void tc30_profileAllTabIsNotEmpty() {
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToAllTab();

        assertTrue(profile.isTabContentNotEmpty(), "Вкладка «Все» в профиле пуста");
    }

    @Test
    @DisplayName("TC-31 [chrome]: Вкладка «Темы» в профиле непуста")
    void tc31_profileTopicsTabIsNotEmpty() {
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToTopicsTab();

        assertTrue(profile.isTabContentNotEmpty(), "Вкладка «Темы» в профиле пуста");
    }

    @Test
    @DisplayName("TC-32 [chrome]: Вкладка «Избранное» в профиле непуста")
    void tc32_profileFavoritesTabIsNotEmpty() {
        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToFavoritesTab();

        assertTrue(profile.isTabContentNotEmpty(), "Вкладка «Избранное» в профиле пуста");
    }
}
