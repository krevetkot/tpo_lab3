package ru.womantest.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.CreateTopicPage;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.ForumTopicPage;
import ru.womantest.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ForumTest Firefox — Форумы")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ForumFirefoxTest extends BaseTest {

    @BeforeAll
    void setUpOnce() {
        setManagedExternally();
        initDriverAndLoginForum("firefox");
    }

    @AfterAll
    void tearDownOnce() {
        quitDriver();
    }

    @Test
    @DisplayName("TC-12 [firefox]: Форум загружается и содержит список тем")
    void tc12_forumHasTopics() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();

        assertTrue(forum.hasTopics(), "Форум не содержит ни одной темы");
        assertTrue(forum.getTopicLinks().size() > 0, "Список тем пуст");
    }

    @Test
    @DisplayName("TC-14 [firefox]: Авторизованный пользователь создаёт тему")
    void tc14_authorizedUserCreatesTopic() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(), "Кнопка создания темы не найдена.");

        CreateTopicPage form = forum.clickCreateTopic();
        assertTrue(form.isFormLoaded(), "Форма создания темы не загрузилась");

        form.fillTitle("Добрый день!");
        form.fillBody("Желаю всем приятного дня!");
        form.submit();

        assertTrue(
                driver().getCurrentUrl().contains("forum"),
                "После создания темы не произошёл переход на форум"
        );
    }

    @Test
    @DisplayName("TC-16 [firefox]: Создание темы без заголовка показывает ошибку")
    void tc16_createTopicWithoutTitleShowsError() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(), "Кнопка создания темы не найдена.");

        CreateTopicPage form = forum.clickCreateTopic();
        form.fillBody("Текст без заголовка");
        form.submitAndExpectError();

        assertTrue(
                form.hasValidationError() || form.isSubmitDisabled(),
                "Форма отправилась без заголовка — ожидалась ошибка валидации"
        );
    }
}
