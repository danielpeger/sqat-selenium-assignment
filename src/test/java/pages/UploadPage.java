package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import util.ConfigReader;

public class UploadPage extends BasePage {

    private final By fileInput = By.cssSelector("input[type='file']");

    public UploadPage(WebDriver driver) {
        super(driver);
    }

    public UploadPage open() {
        driver.get(ConfigReader.get("upload.url"));
        dismissCookieBannerIfPresent();
        return this;
    }

    public boolean hasFileInput() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(fileInput));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public WebElement getFileInput() {
        return wait.until(d -> d.findElement(fileInput));
    }

    public void selectFile(String absolutePath) {
        getFileInput().sendKeys(absolutePath);
    }
}
