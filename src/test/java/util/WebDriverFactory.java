package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Dimension;

public class WebDriverFactory {

    public static WebDriver create() {
        return create(ConfigReader.get("browser", "chrome"));
    }

    public static WebDriver create(String browser) {
        return create(browser, ConfigReader.getBoolean("headless"));
    }

    public static WebDriver create(String browser, boolean headless) {
        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "safari":
                driver = new SafariDriver(new SafariOptions());
                break;
            case "chrome":
            default:
                driver = new ChromeDriver(buildChromeOptions(headless, null));
                break;
        }
        driver.manage().window().maximize();
        driver.manage().window().setSize(new Dimension(1400, 900));
        return driver;
    }

    public static WebDriver createChromeWithDownloadDir(File downloadDir) {
        ChromeDriver driver = new ChromeDriver(buildChromeOptions(false, downloadDir));
        driver.manage().window().maximize();
        return driver;
    }

    public static ChromeOptions buildChromeOptions(boolean headless, File downloadDir) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1400,900");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36 SeleniumTest");
        if (headless) {
            options.addArguments("--headless=new");
        }
        if (downloadDir != null) {
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("download.default_directory", downloadDir.getAbsolutePath());
            prefs.put("download.prompt_for_download", false);
            prefs.put("safebrowsing.enabled", true);
            options.setExperimentalOption("prefs", prefs);
        }
        return options;
    }
}
