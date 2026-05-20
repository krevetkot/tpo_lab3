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

@DisplayName("ForumTest Chrome — Форумы")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ForumChromeTest extends BaseTest {

    @BeforeAll
    void setUpOnce() {
        setManagedExternally();
        initDriverAndLoginForum("chrome");
    }

    @AfterAll
    void tearDownOnce() {
        quitDriver();
    }

    @Test
    @DisplayName("TC-15 [chrome]: Авторизованный пользователь создаёт тему")
    void tc15_authorizedUserCreatesTopic() {
        ForumListPage forum = new ForumListPage(driver(), getWait()).open();

        CreateTopicPage form = forum.clickCreateTopic();
        assertTrue(form.isFormLoaded(), "Форма создания темы не загрузилась");

        form.fillTitle("Круто");
        form.fillBody("Это автоматически созданное сообщение для проверки функционала.");
        form.submit();

        assertTrue(
                driver().getCurrentUrl().contains("forum"),
                "После создания темы не произошёл переход на форум"
        );
    }

    @Test
    @DisplayName("TC-16 [chrome]: Авторизованный пользователь размещает ответ в теме")
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
    @DisplayName("TC-25 [chrome]: Созданная тема появляется в профиле с плашкой «на модерации»")
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
        profile.openViaIcon();
        profile.goToMyProfile();
        profile.goToTopicsTab();

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