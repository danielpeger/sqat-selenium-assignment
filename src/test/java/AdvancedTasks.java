import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import pages.HomePage;
import pages.LoginPage;
import pages.SignupPage;
import pages.UploadPage;
import util.ConfigReader;
import util.TestDataGenerator;
import util.WebDriverFactory;

public class AdvancedTasks {

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
        new LoginPage(driver).open().loginWith(
                ConfigReader.get("user.email"),
                ConfigReader.get("user.password"));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/log_in")));
    }

    @Test
    public void chromeOptionsAreApplied() {
        driver.get(ConfigReader.get("base.url"));
        String userAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        Assert.assertTrue(
                "Custom user agent from WebDriverFactory should be active, was: " + userAgent,
                userAgent.contains("SeleniumTest"));
    }

    @Test
    public void cookiesCanBeReadAddedAndDeleted() {
        driver.get(ConfigReader.get("base.url"));
        int initialCount = driver.manage().getCookies().size();

        driver.manage().addCookie(new Cookie("selenium_marker", "abc123", "/"));
        Cookie added = driver.manage().getCookieNamed("selenium_marker");
        Assert.assertNotNull("Cookie should have been added", added);
        Assert.assertEquals("abc123", added.getValue());

        driver.manage().deleteCookieNamed("selenium_marker");
        Assert.assertNull(
                "Cookie should be gone after deletion",
                driver.manage().getCookieNamed("selenium_marker"));

        driver.manage().deleteAllCookies();
        Assert.assertTrue(
                "All cookies should be cleared, started with " + initialCount,
                driver.manage().getCookies().size() == 0);
    }

    @Test
    public void hoveringHeaderNavigationOpensSubmenu() {
        new HomePage(driver).open();
        WebElement navigationTrigger = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//header//button | //header//a[contains(@href,'/features') or contains(.,'Solutions') or contains(.,'Watch')]")));
        new Actions(driver).moveToElement(navigationTrigger).pause(Duration.ofMillis(500)).perform();
        List<WebElement> revealedLinks = driver.findElements(
                By.xpath("//header//a[contains(@href,'/')]"));
        Assert.assertTrue(
                "Header should expose navigation links after hover",
                revealedLinks.size() > 3);
    }

    @Test
    public void videoScrubberDragAndDrop() {
        loginWithRealCredentials();
        driver.get(ConfigReader.get("video.page.url"));
        By playButton = By.cssSelector("button[data-play-button='true']");
        By slider = By.cssSelector("[data-progress-bar-focus-target='true'][role='slider']");

        WebElement play = wait.until(ExpectedConditions.elementToBeClickable(playButton));
        play.click();

        WebElement scrubber = wait.until(ExpectedConditions.visibilityOfElementLocated(slider));
        String beforeValueRaw = scrubber.getAttribute("aria-valuenow");
        Assert.assertNotNull("Progress slider must expose aria-valuenow", beforeValueRaw);
        double beforeValue = Double.parseDouble(beforeValueRaw);

        new Actions(driver)
                .moveToElement(scrubber)
                .clickAndHold(scrubber)
                .pause(Duration.ofMillis(250))
                .moveByOffset(120, 0)
                .pause(Duration.ofMillis(250))
                .release()
                .perform();

        wait.until(d -> {
            String now = d.findElement(slider).getAttribute("aria-valuenow");
            return now != null && Double.parseDouble(now) > beforeValue;
        });
        double afterValue = Double.parseDouble(driver.findElement(slider).getAttribute("aria-valuenow"));
        Assert.assertTrue(
                "Expected scrubber value to increase after drag. Before=" + beforeValue + ", After=" + afterValue,
                afterValue > beforeValue);
    }

    @Test
    public void fileUploadFieldAcceptsFileAfterLogin() throws IOException {
        Path tempVideo = Files.createTempFile("vimeo-upload-test", ".mp4");
        Files.write(tempVideo, "fake video bytes".getBytes());
        try {
            loginWithRealCredentials();
            UploadPage uploadPage = new UploadPage(driver).open();
            Assert.assertTrue("Upload page should expose a file input", uploadPage.hasFileInput());
            uploadPage.selectFile(tempVideo.toAbsolutePath().toString());
            String value = uploadPage.getFileInput().getDomProperty("value");
            Assert.assertNotNull(value);
            Assert.assertTrue(
                    "File input value should contain the chosen filename, was: " + value,
                    value.toLowerCase().endsWith(".mp4"));
        } finally {
            Files.deleteIfExists(tempVideo);
        }
    }

    @Test
    public void browserBackAndForwardNavigationWorks() {
        driver.get(ConfigReader.get("base.url"));
        String firstUrl = driver.getCurrentUrl();
        driver.get(ConfigReader.get("about.url"));
        String secondUrl = driver.getCurrentUrl();
        driver.navigate().back();
        wait.until(ExpectedConditions.urlToBe(firstUrl));
        Assert.assertEquals(firstUrl, driver.getCurrentUrl());
        driver.navigate().forward();
        wait.until(ExpectedConditions.urlToBe(secondUrl));
        Assert.assertEquals(secondUrl, driver.getCurrentUrl());
    }

    @Test
    public void randomlyGeneratedDataCanBeUsedOnSignupForm() {
        String email = TestDataGenerator.randomEmail();
        String password = TestDataGenerator.randomPassword();
        SignupPage signupPage = new SignupPage(driver).open();
        signupPage.fillEmail(email).clickSubmit().fillPassword(password);
        Assert.assertEquals(email, signupPage.getEmailValue());
        Assert.assertEquals(password, signupPage.getPasswordValue());
    }

    @Test
    public void configurationIsLoadedFromExternalPropertiesFile() {
        String baseUrl = ConfigReader.get("base.url");
        String loginUrl = ConfigReader.get("login.url");
        Assert.assertFalse("base.url should not be empty", baseUrl == null || baseUrl.trim().isEmpty());
        Assert.assertFalse("login.url should not be empty", loginUrl == null || loginUrl.trim().isEmpty());
        Assert.assertTrue(
                "login.url should point to the log_in page",
                loginUrl.equals(baseUrl + "/log_in") || loginUrl.contains("/log_in"));
        driver.get(baseUrl);
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("vimeo"));
    }

    @Test
    public void javascriptExecutorScrollsAndReadsPageState() {
        new HomePage(driver).open();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "var el = document.scrollingElement || document.documentElement || document.body;" +
                        "if (el) { el.scrollTop = el.scrollHeight; }" +
                        "window.scrollTo(0, Math.max(document.body.scrollHeight, document.documentElement.scrollHeight));");

        wait.until(d -> {
            Number stableScroll = (Number) js.executeScript(
                    "return Math.max(" +
                            "window.pageYOffset || 0," +
                            "window.scrollY || 0," +
                            "(document.documentElement && document.documentElement.scrollTop) || 0," +
                            "(document.body && document.body.scrollTop) || 0);");
            return stableScroll.doubleValue() > 0;
        });

        double scrollY = ((Number) js.executeScript(
                "return Math.max(" +
                        "window.pageYOffset || 0," +
                        "window.scrollY || 0," +
                        "(document.documentElement && document.documentElement.scrollTop) || 0," +
                        "(document.body && document.body.scrollTop) || 0);")).doubleValue();

        Assert.assertTrue("Page should have scrolled vertically, scrollY=" + scrollY, scrollY > 0);
        String title = (String) js.executeScript("return document.title;");
        Assert.assertEquals(driver.getTitle(), title);
    }

    @Test
    public void headlessChromeCanLoadHomePage() {
        driver.quit();
        driver = WebDriverFactory.create("chrome", true);
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
        driver.get(ConfigReader.get("base.url"));
        Assert.assertTrue(
                "Headless Chrome should still load Vimeo: " + driver.getTitle(),
                driver.getTitle().toLowerCase().contains("vimeo"));
    }

    @Test
    public void homePageLoadsInChromeBrowser() {
        driver.quit();
        driver = WebDriverFactory.create("chrome", false);
        driver.get(ConfigReader.get("base.url"));
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("vimeo"));
    }

    @Test
    public void homePageLoadsInSafariBrowser() {
        driver.quit();
        driver = WebDriverFactory.create("safari", false);
        driver.get(ConfigReader.get("base.url"));
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("vimeo"));
    }
}
