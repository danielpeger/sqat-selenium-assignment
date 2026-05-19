package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.ConfigReader;

public class SignupPage extends BasePage {

    private final By firstNameInput = By.cssSelector("input[name='first_name'], input[name='firstName'], input#first_name");
    private final By lastNameInput = By.cssSelector("input[name='last_name'], input[name='lastName'], input#last_name");
    private final By emailInput = By.cssSelector(
            "input#email_login, input[type='email'], input[name='email']");
    private final By passwordInput = By.cssSelector("input[type='password'], input[name='password']");
    private final By marketingCheckbox = By.cssSelector("input[type='checkbox']");
    private final By submitButton = By.cssSelector(
            "button[class*='JoinScreen__SubmitButton'], button[type='submit']");

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public SignupPage open() {
        driver.get(ConfigReader.get("signup.url"));
        dismissCookieBannerIfPresent();
        return this;
    }

    public SignupPage fillEmail(String email) {
        WebElement field = waitForVisible(emailInput);
        field.clear();
        field.sendKeys(email);
        return this;
    }

    public SignupPage fillPassword(String password) {
        WebElement field = waitForVisible(passwordInput);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public SignupPage fillFirstName(String firstName) {
        if (isPresent(firstNameInput)) {
            WebElement field = waitForVisible(firstNameInput);
            field.clear();
            field.sendKeys(firstName);
        }
        return this;
    }

    public SignupPage fillLastName(String lastName) {
        if (isPresent(lastNameInput)) {
            WebElement field = waitForVisible(lastNameInput);
            field.clear();
            field.sendKeys(lastName);
        }
        return this;
    }

    public WebElement firstCheckbox() {
        return waitForVisible(marketingCheckbox);
    }

    public boolean isSubmitButtonPresent() {
        return isPresent(submitButton);
    }
}
