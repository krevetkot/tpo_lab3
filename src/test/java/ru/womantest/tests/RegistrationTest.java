package ru.womantest.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.RegistrationPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RegistrationTest — Регистрация")
class RegistrationTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-26: Новый пользователь доходит до подтверждения почты")
    @ValueSource(strings = {"chrome"})
    void tc26_newUserRegistrationStopsAtEmailConfirmation(String browser) {
        initDriver(browser);
        String suffix = String.valueOf(System.currentTimeMillis());
        String email = "womantest" + suffix + "@mail.ru";
        String password = "WomanTest!" + suffix.substring(suffix.length() - 6);
        String nickname = "womantest" + suffix.substring(suffix.length() - 8);

        RegistrationPage registration = new RegistrationPage(driver(), getWait());
        registration.registerUntilEmailConfirmation(email, password, nickname);

        assertTrue(
            registration.isEmailConfirmationStepVisible(),
            "Регистрация не дошла до шага подтверждения через почту"
        );
    }
}
