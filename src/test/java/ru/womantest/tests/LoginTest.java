package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.ForumLoginPage;
import ru.womantest.pages.LoginPage;
import ru.womantest.util.ConfigReader;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginTest — Авторизация")
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-22: Успешная авторизация на главной")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc22_successfulLoginOnMainSite(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email и test.password — тест пропущен");

        LoginPage loginPage = new LoginPage(driver(), getWait());
        loginPage.login(cfg.getEmail(), cfg.getPassword());

        assertTrue(loginPage.isLoggedIn(),
                "Пользователь не авторизовался на главной: аватар не появился после входа");
    }

    @ParameterizedTest(name = "[{0}] TC-23: Неверный пароль на главной показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc23_wrongPasswordOnMainSiteShowsError(String browser) {
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

    @ParameterizedTest(name = "[{0}] TC-24: Несуществующий email на главной показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc24_unknownEmailOnMainSiteShowsError(String browser) {
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

    @ParameterizedTest(name = "[{0}] TC-25: Успешная авторизация на форуме")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc25_successfulLoginOnForum(String browser) {
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

    @ParameterizedTest(name = "[{0}] TC-26: Неверный пароль на форуме показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc26_wrongPasswordOnForumShowsError(String browser) {
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

    @ParameterizedTest(name = "[{0}] TC-27: Несуществующий email на форуме показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc27_unknownEmailOnForumShowsError(String browser) {
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
