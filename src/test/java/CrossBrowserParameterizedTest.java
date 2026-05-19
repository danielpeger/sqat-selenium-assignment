import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import util.ConfigReader;
import util.WebDriverFactory;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CrossBrowserParameterizedTest {

    @Parameterized.Parameters(name = "{index}: browser={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"chrome"},
                {"safari"}
        });
    }

    @Parameterized.Parameter
    public String browser;

    private WebDriver driver;

    @Before
    public void setUp() {
        driver = WebDriverFactory.create(browser, false);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void homePageLoadsInEachConfiguredBrowser() {
        driver.get(ConfigReader.get("base.url"));
        Assert.assertTrue(
                "Home page title should contain Vimeo in browser " + browser + ", was: " + driver.getTitle(),
                driver.getTitle().toLowerCase().contains("vimeo"));
    }
}
