package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchResultsPage extends BasePage {

    private final By resultLinks = By.xpath("//a[contains(@href,'/') and (descendant::img or contains(@class,'result'))]");
    private final By anyVideoThumbnail = By.cssSelector("a[href*='/'] img, a[data-test-id*='result']");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public boolean hasAnyResults() {
        return isPresent(anyVideoThumbnail) || isPresent(resultLinks);
    }

    public List<WebElement> findResults() {
        return driver.findElements(resultLinks);
    }
}
