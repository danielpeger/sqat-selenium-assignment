package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import util.ConfigReader;

public class HomePage extends BasePage {

    private final By signInLink = By.xpath("//a[contains(@href,'/log_in') and not(@aria-hidden='true')]");
    private final By solutionsMenu = By.xpath("//nav//button[contains(.,'Solutions') or contains(.,'Use cases')]");
    private final By solutionsDropdown = By.xpath("//*[@role='menu' or @role='region' or contains(@class,'dropdown')]");
    private final By userMenuButton = By.cssSelector("[data-test-id='topnav-user-menu'], button[aria-label*='profile' i], a[href='/settings']");
    private final By logoutLink = By.xpath("//a[contains(@href,'/log_out') or contains(.,'Log out') or contains(.,'Sign out')]");
    private final By searchInput = By.cssSelector("input[type='search'], input[placeholder*='Search' i], input[name='q']");
    private final By searchInputByRole = By.cssSelector("input[role='search']");
    private final By searchTrigger = By.cssSelector("button[aria-label*='search' i], [data-testid*='search' i], a[href*='/search']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(ConfigReader.get("base.url"));
        dismissCookieBannerIfPresent();
        return this;
    }

    public LoginPage goToLogin() {
        driver.get(ConfigReader.get("login.url"));
        return new LoginPage(driver);
    }

    public SearchResultsPage searchFor(String query) {
        driver.get(ConfigReader.get("base.url") + "/search?q=" +
                query.replace(" ", "+"));
        return new SearchResultsPage(driver);
    }

    public boolean isSignInLinkVisible() {
        return isPresent(signInLink);
    }

    public WebElement openSolutionsMenu() {
        WebElement menu = waitForVisible(solutionsMenu);
        new Actions(driver).moveToElement(menu).perform();
        return waitForVisible(solutionsDropdown);
    }

    public boolean isUserMenuPresent() {
        return isPresent(userMenuButton);
    }

    public void logout() {
        driver.get(ConfigReader.get("base.url") + "/log_out");
    }

    public WebElement getSearchInput() {
        int defaultSelectorCount = driver.findElements(searchInput).size();
        int roleSelectorCount = driver.findElements(searchInputByRole).size();
        int triggerCount = driver.findElements(searchTrigger).size();

        if (defaultSelectorCount == 0 && roleSelectorCount == 0 && triggerCount > 0) {
            WebElement trigger = waitForClickable(searchTrigger);
            trigger.click();
        }

        return waitForVisible(searchInput);
    }
}
