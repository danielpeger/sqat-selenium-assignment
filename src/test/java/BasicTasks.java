import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import pages.HomePage;
import pages.LoginPage;
import pages.SignupPage;
import pages.SearchResultsPage;
import util.ConfigReader;
import util.WebDriverFactory;

public class BasicTasks {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        driver = WebDriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginWithRealCredentials() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.loginWith(ConfigReader.get("user.email"), ConfigReader.get("user.password"));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/log_in")));
    }

    @Test
    public void homePageTitleContainsVimeo() {
        HomePage homePage = new HomePage(driver).open();
        Assert.assertTrue(
                "Home page title should mention Vimeo, was: " + homePage.getTitle(),
                homePage.getTitle().toLowerCase().contains("vimeo"));
    }

    @Test
    public void loginPageHasEmailAndPasswordInputs() {
        new LoginPage(driver).open();
        Assert.assertTrue(driver.findElements(By.cssSelector("input[type='email']")).size() > 0);
        Assert.assertTrue(driver.findElements(By.cssSelector("input[type='password']")).size() > 0);
    }

    @Test
    public void loginFormSubmissionWithValidCredentialsRemovesLoginUrl() {
        loginWithRealCredentials();
        Assert.assertFalse(
                "After login the URL should no longer contain /log_in",
                driver.getCurrentUrl().contains("/log_in"));
    }

    @Test
    public void loginFormSubmissionWithInvalidCredentialsStaysOnLoginPage() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.loginWith("not-a-real-user@example.invalid", "DefinitelyWrongPassword!");
        Assert.assertTrue(
                "Login with bad credentials should not navigate away from /log_in",
                wait.until(ExpectedConditions.urlContains("/log_in")));
    }

    @Test
    public void emailInputAcceptsTextOnLoginPage() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.typeEmail("typed-on-purpose@example.com");
        Assert.assertEquals("typed-on-purpose@example.com", loginPage.getEmailValue());
    }

    @Test
    public void passwordInputAcceptsTextOnLoginPage() {
        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.typePassword("HelloWorld123!");
        WebElement pwd = driver.findElement(By.cssSelector("input[type='password']"));
        Assert.assertEquals("HelloWorld123!", pwd.getDomProperty("value"));
    }

    @Test
    public void searchInputAcceptsTextOnHomePage() {
        HomePage homePage = new HomePage(driver).open();
        WebElement search = homePage.getSearchInput();
        search.sendKeys("nature");
        Assert.assertEquals("nature", search.getDomProperty("value"));
    }

    @Test
    public void signupPageShowsEmailInput() {
        new SignupPage(driver).open().fillEmail("brand-new-user@example.com");
        WebElement email = driver.findElement(By.id("email_login"));
        Assert.assertEquals("brand-new-user@example.com", email.getDomProperty("value"));
    }

    @Test
    public void signupPagePasswordInputCanBeFilled() {
        new SignupPage(driver).open().fillPassword("MyVeryStrongPwd!");
        WebElement pwd = driver.findElement(By.cssSelector("input[type='password']"));
        Assert.assertEquals("MyVeryStrongPwd!", pwd.getDomProperty("value"));
    }

    @Test
    public void signupPageCheckboxCanBeToggled() {
        SignupPage signupPage = new SignupPage(driver).open();
        WebElement checkbox = signupPage.firstCheckbox();
        boolean before = checkbox.isSelected();
        checkbox.click();
        Assert.assertNotEquals(
                "Checkbox state should change after clicking",
                before, checkbox.isSelected());
    }

    @Test
    public void searchFormSubmissionNavigatesToResultsPage() {
        new HomePage(driver).open().searchFor("ocean");
        wait.until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/search"));
    }

    @Test
    public void searchResultsPageShowsResults() {
        SearchResultsPage results = new HomePage(driver).open().searchFor("mountain");
        wait.until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue("Expected at least one search result", results.hasAnyResults());
    }

    @Test
    public void aboutPageContainsCompanyText() {
        driver.get("https://vimeo.com/about");
        wait.until(ExpectedConditions.titleContains("Vimeo"));
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                "About page should mention 'vimeo' somewhere in the page text",
                bodyText.contains("vimeo"));
    }

    @Test
    public void multiplePublicPagesHaveVimeoInTitle() {
        List<String> paths = Arrays.asList(
                "/", "/features", "/upgrade", "/about", "/jobs", "/help");
        String base = ConfigReader.get("base.url");
        for (String path : paths) {
            driver.get(base + path);
            String title = driver.getTitle();
            Assert.assertTrue(
                    "Page " + path + " title did not mention Vimeo, was: " + title,
                    title.toLowerCase().contains("vimeo"));
        }
    }

    @Test
    public void complexXpathFindsLoginLinkInHeader() {
        new HomePage(driver).open();
        List<WebElement> matches = driver.findElements(
                By.xpath("//header//a[contains(@href,'/log_in')]"));
        Assert.assertFalse("Header should contain a link to /log_in", matches.isEmpty());
    }

    @Test
    public void complexXpathFindsJoinLinkInHeader() {
        new HomePage(driver).open();
        List<WebElement> matches = driver.findElements(
                By.xpath("//header//a[contains(@href,'/join') or contains(text(),'Join') or contains(text(),'Sign up')]"));
        Assert.assertFalse("Header should contain a link to join/sign up", matches.isEmpty());
    }

    @Test
    public void complexXpathFindsFooterLinks() {
        new HomePage(driver).open();
        List<WebElement> footerLinks = driver.findElements(
                By.xpath("//footer//a[contains(@href,'/') and string-length(normalize-space(text())) > 0]"));
        Assert.assertTrue(
                "Footer should contain multiple links, found " + footerLinks.size(),
                footerLinks.size() > 3);
    }

    @Test
    public void complexXpathFindsAllImagesUnderMain() {
        new HomePage(driver).open();
        List<WebElement> images = driver.findElements(
                By.xpath("//main//img[@src and not(contains(@src,'data:'))]"));
        Assert.assertFalse("Home page main area should contain images", images.isEmpty());
    }

    @Test
    public void complexXpathOnSignupPageFindsSubmitButton() {
        new SignupPage(driver).open();
        List<WebElement> matches = driver.findElements(
                By.xpath("//form//button[@type='submit'][1]"));
        Assert.assertFalse("Signup form should expose a submit button", matches.isEmpty());
    }

    @Test
    public void explicitWaitWaitsForLoginFormToBeVisible() {
        driver.get(ConfigReader.get("login.url"));
        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")));
        Assert.assertTrue("Email field should be displayed", emailField.isDisplayed());
    }

    @Test
    public void textareaOnSettingsPageCanBeFilledAfterLogin() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("settings.url"));
        WebElement textarea = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("textarea")));
        String bio = "Selenium test bio " + System.currentTimeMillis();
        textarea.clear();
        textarea.sendKeys(bio);
        Assert.assertEquals(bio, textarea.getDomProperty("value"));
    }

    @Test
    public void dropdownOnSettingsPageCanBeUsedAfterLogin() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("settings.url"));
        WebElement selectElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.tagName("select")));
        Select dropdown = new Select(selectElement);
        Assert.assertFalse("Dropdown should expose options", dropdown.getOptions().isEmpty());
        dropdown.selectByIndex(0);
        Assert.assertNotNull(dropdown.getFirstSelectedOption().getText());
    }

    @Test
    public void radioButtonOnUpgradePageCanBeSelected() {
        driver.get("https://vimeo.com/upgrade");
        List<WebElement> radios = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("input[type='radio']")));
        Assert.assertFalse("Upgrade page should expose at least one radio input", radios.isEmpty());
        WebElement firstRadio = radios.get(0);
        Assert.assertTrue("Radio input element should be enabled", firstRadio.isEnabled());
    }

    @Test
    public void settingsFormCanBeSubmittedByLoggedInUser() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("settings.url"));
        WebElement bio = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("textarea")));
        bio.clear();
        bio.sendKeys("Selenium-updated bio " + System.currentTimeMillis());
        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and (contains(.,'Save') or contains(.,'save'))]")));
        saveButton.click();
        WebElement saved = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'SAVED','saved'),'saved') or @role='status' or contains(@class,'success')]")));
        Assert.assertNotNull(saved);
    }

    @Test
    public void logoutAfterLoginRedirectsToPublicArea() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("base.url") + "/log_out");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@href,'/log_in')]")));
        Assert.assertTrue(
                "After logout the page must show a link back to /log_in",
                driver.findElements(By.xpath("//a[contains(@href,'/log_in')]")).size() > 0);
    }
}
