import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class TaskSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private final By usernameInputLocator = By.id("username");
    private final By passwordInputLocator = By.id("password");
    private final By loginFormLocator = By.id("login");
    private final By flashSuccessLocator = By.cssSelector("div.flash.success");
    private final By flashErrorLocator = By.cssSelector("div.flash.error");
    private final By logoutLinkLocator = By.cssSelector("a[href='/logout']");


    private WebElement waitVisibilityAndFindElement(By locator) {
        this.wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return this.driver.findElement(locator);
    }

    private void login(String username, String password) {
        WebElement usernameInput = waitVisibilityAndFindElement(usernameInputLocator);
        usernameInput.clear();
        usernameInput.sendKeys(username);

        WebElement passwordInput = waitVisibilityAndFindElement(passwordInputLocator);
        passwordInput.clear();
        passwordInput.sendKeys(password);

        String usernameValue = usernameInput.getDomProperty("value");
        String passwordValue = passwordInput.getDomProperty("value");
        if (!username.equals(usernameValue) || !password.equals(passwordValue)) {
            ((JavascriptExecutor) this.driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[2].value = arguments[3];",
                    usernameInput, username, passwordInput, password);
        }

        WebElement loginForm = waitVisibilityAndFindElement(loginFormLocator);
        loginForm.submit();
    }

    @Test
    public void testTheInternet() {
        this.driver.get("http://the-internet.herokuapp.com/login");

        login("tomsmith", "SuperSecretPassword!");

        this.wait.until(ExpectedConditions.urlContains("/secure"));
        WebElement flashSuccess = waitVisibilityAndFindElement(flashSuccessLocator);
        Assert.assertTrue(flashSuccess.getText().contains("You logged into a secure area!"));

        WebElement logoutLink = waitVisibilityAndFindElement(logoutLinkLocator);
        this.driver.get(logoutLink.getDomProperty("href"));

        this.wait.until(ExpectedConditions.urlContains("/login"));
        waitVisibilityAndFindElement(usernameInputLocator);

        login("tomsmith", "WrongPassword!");

        WebElement flashError = waitVisibilityAndFindElement(flashErrorLocator);
        Assert.assertTrue(flashError.getText().contains("Your password is invalid!"));
    }

    @After
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
