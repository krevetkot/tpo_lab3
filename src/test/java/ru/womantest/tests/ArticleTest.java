package ru.womantest.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.ArticlePage;
import ru.womantest.pages.MainPage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ArticleTest — Статьи, теги, похожие материалы")
class ArticleTest extends BaseTest {

    private ArticlePage openArticle(String browser) {
        initDriver(browser);
        return new MainPage(driver(), getWait()).openFirstArticle();
    }

    @ParameterizedTest(name = "[{0}] TC-08: Статья содержит заголовок h1 и текст")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc08_articleHasTitleAndBody(String browser) {
        ArticlePage article = openArticle(browser);

        assertFalse(article.getTitle().isBlank(), "Заголовок статьи пустой");
        assertTrue(article.hasBody(), "Тело статьи отсутствует или пустое");
    }

    @ParameterizedTest(name = "[{0}] TC-09: На странице статьи присутствует блок комментариев")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc09_articleHasCommentsSection(String browser) {
        ArticlePage article = openArticle(browser);

        assertTrue(article.hasComments(), "Блок комментариев не найден на странице статьи");
    }

    @ParameterizedTest(name = "[{0}] TC-10: Клик по тегу ведёт на страницу тега")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc10_tagClickNavigatesToTagPage(String browser) {
        ArticlePage article = openArticle(browser);
        Assumptions.assumeTrue(article.hasTags(), "У статьи нет тегов — тест пропущен");

        String urlBefore = driver().getCurrentUrl();
        String urlAfter = article.clickFirstTagAndGetUrl();

        assertNotEquals(urlBefore, urlAfter, "URL не изменился после клика по тегу");
    }

    @ParameterizedTest(name = "[{0}] TC-12: Под статьёй отображается блок похожих материалов")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc12_articleHasRelatedLinks(String browser) {
        ArticlePage article = openArticle(browser);

        assertTrue(article.hasRelatedLinks(), "Блок похожих материалов не найден");
        assertTrue(article.getRelatedLinkCount() > 0, "Блок похожих материалов пустой");
    }
}
