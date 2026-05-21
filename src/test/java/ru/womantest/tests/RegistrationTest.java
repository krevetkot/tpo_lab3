package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.RegistrationPage;
import ru.womantest.util.ConfigReader;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RegistrationTest — Регистрация")
class RegistrationTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-21: Регистрация нового пользователя")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc21_newUserRegistrationStopsAtEmailConfirmation(String browser) {
        initDriver(browser);
        new ForumListPage(driver(), getWait()).open();
        String suffix = String.valueOf(System.currentTimeMillis());
        String email = "womantest" + suffix + "@mail.ru";
        String password = "WomanTest!" + suffix.substring(suffix.length() - 6);
        String nickname = "womantest" + suffix.substring(suffix.length() - 8);

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.register(email, password, nickname);

        assertTrue(
                registration.isLoggedIn(),
            "Регистрация успешно пройдена"
        );
    }

    @ParameterizedTest(name = "[{0}] TC-28: Регистрация с уже существующим email показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc28_registrationWithExistingEmailShowsError(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email — тест пропущен");
        new ForumListPage(driver(), getWait()).open();

        String suffix = String.valueOf(System.currentTimeMillis());
        String password = cfg.getEmail();
        String nickname = "womantest" + suffix.substring(suffix.length() - 8);

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.tryRegisterWithExistingEmail(cfg.getEmail(), nickname);

        assertTrue(registration.hasExistingEmailError(),
                "Ошибка о существующем email не отображается");
    }

    @ParameterizedTest(name = "[{0}] TC-29: Регистрация с уже существующим никнеймом показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc29_registrationWithExistingNicknameShowsError(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.nickname — тест пропущен");
        new ForumListPage(driver(), getWait()).open();

        String suffix = String.valueOf(System.currentTimeMillis());
        String email = "womantest" + suffix + "@mail.ru";
        String password = cfg.getNickname();

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.tryRegisterWithExistingNickname(email, cfg.getNickname());

        assertTrue(registration.hasExistingNicknameError(),
                "Ошибка о существующем никнейме не отображается");
    }

    static Stream<String> invalidEmails() {
        return Stream.of(
                "ab",
                "a@",
                "@mail.ru",
                "user@ma il.ru",
                "user<>@mail.ru",
                "user[]@mail.ru",
                "user{}@mail.ru",
                "a".repeat(51) + "@mail.ru"
        );
    }

    static Stream<String> invalidNicknames() {
        return Stream.of(
                "ab",
                "a".repeat(51),
                "nick name1236734s",
                "nick<name>tg34tv",
                "nick{name}2c3r2cg2",
                "nick[name]c23r2",
                "nick@name23r2c3",
                ""
        );
    }

    @ParameterizedTest(name = "TC-30: Невалидный email при регистрации — [{0}]")
    @MethodSource("invalidEmails")
    void tc30_invalidEmailShowsError(String email) {
        initDriver("chrome");
        new ForumListPage(driver(), getWait()).open();

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.tryRegisterWithExistingEmail(email,  "validNick123"+System.currentTimeMillis());

        assertFalse(registration.isLoggedIn(),
                "Зарегистрировался с невалидным email: " + email);
        assertTrue(registration.hasExistingEmailError(),
                "Ошибка не отображается для email: " + email);
    }

    @ParameterizedTest(name = "TC-31: Невалидный никнейм при регистрации — [{0}]")
    @MethodSource("invalidNicknames")
    void tc31_invalidNicknameShowsError(String nickname) {
        initDriver("chrome");
        new ForumListPage(driver(), getWait()).open();

        String suffix = String.valueOf(System.currentTimeMillis());
        String email = "womantest" + suffix + "@mail.ru";

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.tryRegisterWithExistingNickname(email, nickname);

        assertFalse(registration.isLoggedIn(),
                "Зарегистрировался с невалидным никнеймом: " + nickname);
        assertTrue(registration.hasExistingNicknameError(),
                "Ошибка не отображается для никнейма: " + nickname);
    }
}
