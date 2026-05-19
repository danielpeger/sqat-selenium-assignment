package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
        return isPresent(fileInput);
    }

    public WebElement getFileInput() {
        return wait.until(d -> d.findElement(fileInput));
    }

    public void selectFile(String absolutePath) {
        getFileInput().sendKeys(absolutePath);
    }
}
