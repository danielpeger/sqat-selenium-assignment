package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.ConfigReader;

public class LoginPage extends BasePage {

    private final By emailInput = By.cssSelector(
            "input[data-testid='site_login_email_input'], input[type='email'], input[name='email'], input#email");
    private final By passwordInput = By.cssSelector(
            "input[data-testid='site_login_password_input'], input[type='password'], input[name='password'], input#password");
    private final By submitButton = By.cssSelector(
            "button[data-testid='site_login_submit_button'], button[type='submit']");
    private final By errorMessage = By.xpath("//*[contains(@class,'error') or contains(@class,'Error') or contains(@role,'alert')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigReader.get("login.url"));
        dismissCookieBannerIfPresent();
        return this;
    }

    public LoginPage typeEmail(String email) {
        WebElement field = waitForVisible(emailInput);
        field.clear();
        field.sendKeys(email);
        return this;
    }

    public LoginPage typePassword(String password) {
        WebElement field = waitForVisible(passwordInput);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public void submit() {
        waitForClickable(submitButton).click();
    }

    public void loginWith(String email, String password) {
        typeEmail(email);
        typePassword(password);
        submit();
    }

    public boolean hasErrorMessage() {
        return isPresent(errorMessage);
    }

    public String getEmailValue() {
        return waitForVisible(emailInput).getDomProperty("value");
    }
}
