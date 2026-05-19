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
import util.ConfigReader;
import util.TestDataGenerator;
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
        LoginPage loginPage = new LoginPage(driver).open();
        Assert.assertTrue(loginPage.hasEmailInput());
        Assert.assertTrue(loginPage.hasPasswordInput());
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
        Assert.assertEquals("HelloWorld123!", loginPage.getPasswordValue());
    }

    @Test
    public void searchInputAcceptsTextOnHomePage() {
        loginWithRealCredentials();
        HomePage homePage = new HomePage(driver).open();
        WebElement search = homePage.getSearchInput();
        search.sendKeys("nature");
        Assert.assertEquals("nature", search.getDomProperty("value"));
    }

    @Test
    public void signupPageShowsEmailInput() {
        SignupPage signupPage = new SignupPage(driver).open().fillEmail("brand-new-user@example.com");
        Assert.assertEquals("brand-new-user@example.com", signupPage.getEmailValue());
    }

    @Test
    public void signupPagePasswordInputCanBeFilled() {
        SignupPage signupPage = new SignupPage(driver)
                .open()
                .fillEmail(TestDataGenerator.randomEmail())
                .clickSubmit()
                .fillPassword("MyVeryStrongPwd!");
        Assert.assertEquals("MyVeryStrongPwd!", signupPage.getPasswordValue());
    }

    @Test
    public void uploadDefaultsPageContainsHideStatsCheckbox() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("upload.defaults.url"));
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='checkbox'].iris_checkbox__input[name='hide_stats']")));
        Assert.assertNotNull("Upload defaults page should contain hide_stats checkbox", checkbox);
    }

    @Test
    public void searchFormSubmissionNavigatesToResultsPage() {
        new HomePage(driver).open().searchFor("ocean");
        wait.until(ExpectedConditions.urlContains("/search"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/search"));
    }

    @Test
    public void aboutPageContainsCompanyText() {
        driver.get(ConfigReader.get("about.url"));
        wait.until(ExpectedConditions.titleContains("Vimeo"));
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                "About page should mention 'vimeo' somewhere in the page text",
                bodyText.contains("vimeo"));
    }

    @Test
    public void multiplePublicPagesHaveVimeoInTitle() {
        List<String> paths = Arrays.asList(
                "/", "/about", "/jobs", "/help");
        String base = ConfigReader.get("base.url");
        for (String path : paths) {
            driver.get(base + path);
            String title = driver.getTitle();
            String titleLower = title.toLowerCase();
            Assert.assertTrue(
                    "Page " + path + " title did not mention Vimeo, video, or pricing, was: " + title,
                    titleLower.contains("vimeo"));
        }
    }

    @Test
    public void complexXpathFindsJoinLinkInHeaderSimple() {
        new HomePage(driver).open();
        List<WebElement> matches = driver.findElements(
                By.xpath("//header//a[contains(@href,'/join')]"));
        Assert.assertFalse("Header should contain a link to /join", matches.isEmpty());
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
    public void complexXpathFindsVideoUnderAssetContainer() {
        new HomePage(driver).open();
        List<WebElement> videos = driver.findElements(
                By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' asset-container ')]//video"));
        Assert.assertFalse("Home page asset-container should contain a video", videos.isEmpty());
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
        LoginPage loginPage = new LoginPage(driver).open();
        Assert.assertTrue(
                "Email field should be displayed",
                wait.until(d -> loginPage.hasEmailInput()));
    }

    @Test
    public void textareaOnProfilePageCanBeFilledAfterLogin() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("profile.url"));
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
    public void radioButtonOnSettingsPageCanBeSelected() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("settings.url"));
        List<WebElement> radios = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("input[type='radio']")));
        Assert.assertFalse("Settings page should expose at least one radio input", radios.isEmpty());
        WebElement firstRadio = radios.get(0);
        Assert.assertTrue("Radio input element should be enabled", firstRadio.isEnabled());
    }

    @Test
    public void settingsFormCanBeSubmittedByLoggedInUser() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("profile.url"));
        WebElement bio = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("textarea")));
        bio.clear();
        bio.sendKeys("Selenium-updated bio " + System.currentTimeMillis());
        By saveButtonLocator = By.cssSelector("input[type='submit'][name='submit'][value='Save']");
        WebElement saveButton = wait.until(d -> {
            WebElement candidate = d.findElement(saveButtonLocator);
            String disabled = candidate.getAttribute("disabled");
            return candidate.isDisplayed() && candidate.isEnabled() && disabled == null ? candidate : null;
        });
        saveButton.click();
        WebElement saved = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'SAVED','saved'),'saved') or @role='status' or contains(@class,'success')]")));
        Assert.assertNotNull(saved);
    }

    @Test
    public void logoutAfterLoginRedirectsToPublicArea() {
        loginWithRealCredentials();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        By userMenuToggleLocator = By.cssSelector("[data-id='account_menu_button']");
        WebElement userMenuToggle = wait.until(ExpectedConditions.elementToBeClickable(userMenuToggleLocator));
        userMenuToggle.click();
        WebElement logoutControl = wait.until(ExpectedConditions.elementToBeClickable(By.id("log-out")));
        logoutControl.click();

        String baseUrl = ConfigReader.get("base.url");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlToBe(baseUrl),
                ExpectedConditions.urlToBe(baseUrl + "/")));
        Assert.assertTrue(
                "After logout the user should be redirected to the home page",
                driver.getCurrentUrl().equals(baseUrl) || driver.getCurrentUrl().equals(baseUrl + "/"));
    }
}
