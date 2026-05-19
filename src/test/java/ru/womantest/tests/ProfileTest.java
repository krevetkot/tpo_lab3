package ru.womantest.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.womantest.base.BaseTest;
import ru.womantest.pages.LoginPage;
import ru.womantest.pages.ProfilePage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] TC-21: Добавить статью в избранное «Избранном»")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc21_favoriteAppearsInFavoritesSection(String browser) {

        initDriver(browser);

        driver().get("https://www.woman.ru/forum/?sort=new");

        LoginPage login = new LoginPage(driver(), getWait());
        login.login("ваш_логин", "ваш_пароль");

        WebElement firstPostLink = getWait().until(d ->
                d.findElement(By.xpath("//a[contains(@class,'list-item__link')]"))
        );

        String postTitle = firstPostLink.findElement(By.xpath(".//span[contains(@class,'list-item__title')]")).getText();

        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView({block:'center'});", firstPostLink);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", firstPostLink);

        WebElement favoriteButton = getWait().until(d -> {
            WebElement btn = d.findElement(By.xpath("//button[contains(@class,'card__favorite')]"));
            return (btn.isDisplayed() && btn.isEnabled()) ? btn : null;
        });

        ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", favoriteButton);
    }

    @ParameterizedTest(name = "[{0}] TC-24: Проверка контента профиля на форуме")
    @ValueSource(strings = {"chrome", "firefox"})
    void tc24_profileHasPostsAndFavorites(String browser) {

        initDriver(browser);

        driver().get("https://www.woman.ru/forum/");

        LoginPage login = new LoginPage(driver(), getWait());
        login.login("ваш_логин", "ваш_пароль");

        ProfilePage profile = new ProfilePage(driver(), getWait());
        profile.openProfileDirect();

        profile.goToMyPosts();
        List<WebElement> myPosts = driver().findElements(By.xpath("//a[contains(@class,'list-item__link')]"));
        assertTrue(myPosts.size() > 0, "Раздел 'Темы' пуст!");

        profile.goToFavorites();
        List<WebElement> favorites = driver().findElements(By.xpath("//a[contains(@class,'list-item__link')]"));
        assertTrue(favorites.size() > 0, "Раздел 'Избранное' пуст!");
    }
}