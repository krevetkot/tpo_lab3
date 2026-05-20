package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.ForumLoginPage;
import ru.womantest.pages.LoginPage;
import ru.womantest.util.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginTest — Авторизация")
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-27: Успешная авторизация на главной")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc27_successfulLoginOnMainSite(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email и test.password — тест пропущен");

        LoginPage loginPage = new LoginPage(driver(), getWait());
        loginPage.login(cfg.getEmail(), cfg.getPassword());

        assertTrue(loginPage.isLoggedIn(),
                "Пользователь не авторизовался на главной: аватар не появился после входа");
    }

    @ParameterizedTest(name = "[{0}] TC-28: Неверный пароль на главной показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc28_wrongPasswordOnMainSiteShowsError(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email — тест пропущен");

        LoginPage loginPage = new LoginPage(driver(), getWait());
        loginPage.loginAndWaitForResult(cfg.getEmail(), "WrongPassword_!@#999");

        assertFalse(loginPage.isLoggedIn(),
                "Пользователь залогинился с неверным паролем на главной — этого не должно быть");
        assertTrue(loginPage.hasLoginError(),
                "Ошибка авторизации не отображается на главной при неверном пароле");
    }

    @ParameterizedTest(name = "[{0}] TC-29: Несуществующий email на главной показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc29_unknownEmailOnMainSiteShowsError(String browser) {
        initDriver(browser);

        LoginPage loginPage = new LoginPage(driver(), getWait());
        loginPage.loginAndWaitForResult(
                "nonexistent_user_xyz_99999@example.com",
                "SomePassword!123"
        );

        assertFalse(loginPage.isLoggedIn(),
                "Пользователь залогинился с несуществующим email на главной");
        assertTrue(loginPage.hasLoginError(),
                "Ошибка авторизации не отображается на главной при несуществующем email");
    }

    @ParameterizedTest(name = "[{0}] TC-30: Успешная авторизация на форуме")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc30_successfulLoginOnForum(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email и test.password — тест пропущен");

        new ForumListPage(driver(), getWait()).open();

        ForumLoginPage forumLogin = new ForumLoginPage(driver(), getWait());
        forumLogin.login(cfg.getEmail(), cfg.getPassword());

        assertTrue(forumLogin.isLoggedIn(),
                "Пользователь не авторизовался на форуме: кнопка «Войти» не исчезла");
    }

    @ParameterizedTest(name = "[{0}] TC-31: Неверный пароль на форуме показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc31_wrongPasswordOnForumShowsError(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email — тест пропущен");

        new ForumListPage(driver(), getWait()).open();

        ForumLoginPage forumLogin = new ForumLoginPage(driver(), getWait());
        forumLogin.loginAndWaitForResult(cfg.getEmail(), "WrongPassword_!@#999");

        assertFalse(forumLogin.isLoggedIn(),
                "Пользователь залогинился на форуме с неверным паролем");
        assertTrue(forumLogin.hasLoginError(),
                "Ошибка авторизации на форуме не отображается при неверном пароле");
    }

    @ParameterizedTest(name = "[{0}] TC-32: Несуществующий email на форуме показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc32_unknownEmailOnForumShowsError(String browser) {
        initDriver(browser);

        new ForumListPage(driver(), getWait()).open();

        ForumLoginPage forumLogin = new ForumLoginPage(driver(), getWait());
        forumLogin.loginAndWaitForResult(
                "nonexistent_user_xyz_99999@example.com",
                "SomePassword!123"
        );

        assertFalse(forumLogin.isLoggedIn(),
                "Пользователь залогинился на форуме с несуществующим email");
        assertTrue(forumLogin.hasLoginError(),
                "Ошибка авторизации на форуме не отображается при несуществующем email");
    }
}