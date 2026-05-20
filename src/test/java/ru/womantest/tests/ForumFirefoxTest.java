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
    @DisplayName("TC-13 [firefox]: Форум загружается и содержит список тем")
    void tc13_forumHasTopics() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();

        assertTrue(forum.hasTopics(), "Форум не содержит ни одной темы");
        assertTrue(forum.getTopicLinks().size() > 0, "Список тем пуст");
    }

    @Test
    @DisplayName("TC-14 [firefox]: Открытие темы показывает список сообщений")
    void tc14_topicHasMessages() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasTopics(), "Нет тем — тест пропущен");

        ForumTopicPage topic = forum.openFirstTopic();

        assertTrue(topic.hasMessages(), "В теме форума нет сообщений");
    }

    @Test
    @DisplayName("TC-15 [firefox]: Авторизованный пользователь создаёт тему")
    void tc15_authorizedUserCreatesTopic() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(), "Кнопка создания темы не найдена.");

        CreateTopicPage form = forum.clickCreateTopic();
        assertTrue(form.isFormLoaded(), "Форма создания темы не загрузилась");

        form.fillTitle("Добрый день!");
        form.fillBody("Желаю всем приятного дня!");
        ForumTopicPage created = form.submitAndExpectSuccess();

        assertTrue(created.hasMessages(), "Созданная тема не содержит сообщений");
    }

    @Test
    @DisplayName("TC-16 [firefox]: Авторизованный пользователь размещает ответ в теме")
    void tc16_authorizedUserPostsReply() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasTopics(), "Нет тем — тест пропущен");

        ForumTopicPage topic = forum.openFirstTopic();
        Assumptions.assumeTrue(topic.hasReplyForm(), "Форма ответа не найдена.");

        int countBefore = topic.getMessageCount();
        topic.postReply("Круто");
        int countAfter = topic.getMessageCount();

        assertTrue(countAfter > countBefore, "Количество сообщений не увеличилось после отправки ответа");
    }

    @Test
    @DisplayName("TC-17 [firefox]: Создание темы без заголовка показывает ошибку")
    void tc17_createTopicWithoutTitleShowsError() {
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

    @Test
    @DisplayName("TC-25 [firefox]: Созданная тема появляется в профиле с плашкой «на модерации»")
    void tc25_createdTopicAppearsInProfileWithModerationBadge() {
        String uniqueTitle = "Всем привет!";

        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(), "Кнопка создания темы не найдена — тест пропущен");

        CreateTopicPage form = forum.clickCreateTopic();
        Assumptions.assumeTrue(form.isFormLoaded(), "Форма создания темы не загрузилась — тест пропущен");

        form.fillTitle(uniqueTitle);
        form.fillBody("Сегодня замечательный день!");
        form.submit();

        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.goToMyProfile();
        profile.goToTopicsTab();
        profile.goToAllTab();

        assertTrue(
                profile.hasTopicWithTitle(uniqueTitle),
                "Созданная тема «" + uniqueTitle + "» не найдена в разделе «Темы - Все»"
        );
        assertTrue(
                profile.topicHasModerationBadge(uniqueTitle),
                "На теме «" + uniqueTitle + "» нет плашки «на модерации»"
        );
    }
}