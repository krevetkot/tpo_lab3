package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.CreateTopicPage;
import ru.womantest.pages.ForumListPage;
import ru.womantest.pages.ForumTopicPage;
import ru.womantest.pages.ProfilePage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ForumTest — Форумы")
class ForumTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-13: Форум загружается и содержит список тем")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc13_forumHasTopics(String browser) {
        initDriver(browser);
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();

        assertTrue(forum.hasTopics(), "Форум не содержит ни одной темы");
        assertTrue(forum.getTopicLinks().size() > 0, "Список тем пуст");
    }

    @ParameterizedTest(name = "[{0}] TC-14: Открытие темы показывает список сообщений")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc14_topicHasMessages(String browser) {
        initDriver(browser);
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasTopics(), "Нет тем — тест пропущен");

        ForumTopicPage topic = forum.openFirstTopic();

        assertTrue(topic.hasMessages(), "В теме форума нет сообщений");
    }

    @ParameterizedTest(name = "[{0}] TC-15: Авторизованный пользователь создаёт тему")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc15_authorizedUserCreatesTopic(String browser) {
        initDriverAndLogin(browser);
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(),
            "Кнопка создания темы не найдена.");

        CreateTopicPage form = forum.clickCreateTopic();
        assertTrue(form.isFormLoaded(), "Форма создания темы не загрузилась");

        form.fillTitle("Круто");
        form.fillBody("Это автоматически созданное сообщение для проверки функционала.");
        ForumTopicPage created = form.submitAndExpectSuccess();

        assertTrue(created.hasMessages(), "Созданная тема не содержит сообщений");
    }

    @ParameterizedTest(name = "[{0}] TC-16: Авторизованный пользователь размещает ответ в теме")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc16_authorizedUserPostsReply(String browser) {
        initDriverAndLogin(browser);
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasTopics(), "Нет тем — тест пропущен");

        ForumTopicPage topic = forum.openFirstTopic();
        Assumptions.assumeTrue(topic.hasReplyForm(),
            "Форма ответа не найдена.");

        int countBefore = topic.getMessageCount();
        topic.postReply("Круто");
        int countAfter = topic.getMessageCount();

        assertTrue(countAfter > countBefore, "Количество сообщений не увеличилось после отправки ответа");
    }

    @ParameterizedTest(name = "[{0}] TC-17: Создание темы без заголовка показывает ошибку")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc17_createTopicWithoutTitleShowsError(String browser) {
        initDriverAndLogin(browser);
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(),
            "Кнопка создания темы не найдена — возможно, XPath нужно скорректировать");

        CreateTopicPage form = forum.clickCreateTopic();
        form.fillBody("Текст без заголовка");
        form.submitAndExpectError();

        assertTrue(
            form.hasValidationError() || form.isSubmitDisabled(),
            "Форма отправилась без заголовка — ожидалась ошибка валидации"
        );
    }

    @ParameterizedTest(name = "[{0}] TC-25: Созданная тема появляется в профиле с плашкой «на модерации»")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc25_createdTopicAppearsInProfileWithModerationBadge(String browser) {
        initDriverAndLogin(browser);

        String uniqueTitle = "Всем привет!";

        ForumListPage forum = new ForumListPage(driver(), getWait()).open();
        Assumptions.assumeTrue(forum.hasCreateTopicButton(),
            "Кнопка создания темы не найдена — тест пропущен");

        CreateTopicPage form = forum.clickCreateTopic();
        Assumptions.assumeTrue(form.isFormLoaded(),
            "Форма создания темы не загрузилась — тест пропущен");

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
