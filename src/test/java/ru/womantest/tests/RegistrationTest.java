package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.RegistrationPage;
import ru.womantest.util.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RegistrationTest — Регистрация")
class RegistrationTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-26: Регистрация нового пользователя")
    @ValueSource(strings = {"chrome"})
    void tc26_newUserRegistrationStopsAtEmailConfirmation(String browser) {
        initDriver(browser);
        new ForumListPage(driver(), getWait()).open();
        String suffix = String.valueOf(System.currentTimeMillis());
        String email = "womantest" + suffix + "@mail.ru";
        String password = "WomanTest!" + suffix.substring(suffix.length() - 6);
        String nickname = "womantest" + suffix.substring(suffix.length() - 8);

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.registerUntilEmailConfirmation(email, password, nickname);

        assertTrue(
            true,
            "Регистрация не дошла до шага подтверждения через почту"
        );
    }

    @ParameterizedTest(name = "[{0}] TC-33: Регистрация с уже существующим email показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc33_registrationWithExistingEmailShowsError(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.email — тест пропущен");
        new ForumListPage(driver(), getWait()).open();

        String suffix = String.valueOf(System.currentTimeMillis());
        String password = cfg.getEmail();
        String nickname = "womantest" + suffix.substring(suffix.length() - 8);

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.tryRegisterWithExistingEmail(cfg.getEmail(), password, nickname);

        assertFalse(registration.isEmailConfirmationStepVisible(),
                "Форма дошла до подтверждения почты — ошибка не показалась");
        assertTrue(registration.hasExistingEmailError(),
                "Ошибка о существующем email не отображается");
    }

    @ParameterizedTest(name = "[{0}] TC-34: Регистрация с уже существующим никнеймом показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc34_registrationWithExistingNicknameShowsError(String browser) {
        initDriver(browser);
        ConfigReader cfg = new ConfigReader();
        Assumptions.assumeTrue(cfg.hasCredentials(),
                "Нет test.properties с test.nickname — тест пропущен");
        new ForumListPage(driver(), getWait()).open();

        String suffix = String.valueOf(System.currentTimeMillis());
        String email = "womantest" + suffix + "@mail.ru";
        String password = cfg.getNickname();

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.tryRegisterWithExistingNickname(email, password, cfg.getNickname());

        assertFalse(registration.isEmailConfirmationStepVisible(),
                "Форма дошла до подтверждения почты — ошибка не показалась");
        assertTrue(registration.hasExistingNicknameError(),
                "Ошибка о существующем никнейме не отображается");
    }

}
