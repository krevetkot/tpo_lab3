package ru.womantest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BasePage {

    private final By loginBtn = By.xpath(
        "//a[contains(@class,'login') or contains(@href,'login') or contains(text(),'Войти')]"
        + " | //button[contains(text(),'Войти')]"
    );

    private final By emailInput = By.xpath(
        "//input[@type='email' or @name='email' or @name='login' or @placeholder[contains(.,'mail')]]"
    );

    private final By passwordInput = By.xpath(
        "//input[@type='password']"
    );

    private final By submitBtn = By.xpath(
        "//button[@type='submit' or contains(@class,'submit') or contains(text(),'Войти')]"
    );

    private final By userAvatar = By.xpath(
        "//a[contains(@class,'avatar') or contains(@class,'profile') or contains(@class,'user')]"
        + " | //div[contains(@class,'user-name')]"
    );

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void login(String email, String password) {
        waitClickable(loginBtn).click();
        waitVisible(emailInput).sendKeys(email);
        waitVisible(passwordInput).sendKeys(password);
        waitClickable(submitBtn).click();
        waitVisible(userAvatar);
    }

    public boolean isLoggedIn() {
        return isPresent(userAvatar);
    }
}
