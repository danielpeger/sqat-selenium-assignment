package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public void dismissCookieBannerIfPresent() {
        if (tryClick(By.id("onetrust-accept-btn-handler"))) {
            return;
        }

        List<By> transcendAcceptButtons = List.of(
                By.cssSelector("#transcend-consent-manager button"),
                By.cssSelector("transcend-consent-manager button"),
                By.cssSelector("button[data-testid*='accept' i]"),
                By.cssSelector("button[id*='accept' i]"));
        for (By locator : transcendAcceptButtons) {
            if (tryClick(locator)) {
                return;
            }
        }

        clickTranscendAcceptFromShadowRoot();
        clickTranscendAcceptInIframes();
    }

    private boolean tryClick(By locator) {
        if (!isPresent(locator)) {
            return false;
        }
        try {
            waitForClickable(locator).click();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void clickTranscendAcceptFromShadowRoot() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "const host = document.querySelector('transcend-consent-manager, #transcend-consent-manager');" +
                            "if (!host) return false;" +
                            "const root = host.shadowRoot || host;" +
                            "let btn = root.querySelector(\"button[data-testid*='accept' i], button[id*='accept' i], button[aria-label*='accept' i]\");" +
                            "if (!btn) {" +
                            "  btn = Array.from(root.querySelectorAll('button')).find((b) => /accept/i.test((b.textContent || '').trim()));" +
                            "}" +
                            "if (!btn) return false;" +
                            "btn.click();" +
                            "return true;");
        } catch (Exception ignored) {
        }
    }

    private void clickTranscendAcceptInIframes() {
        List<WebElement> frames = driver.findElements(
                By.cssSelector("iframe[id*='transcend' i], iframe[src*='transcend' i], iframe[title*='consent' i]"));
        for (WebElement frame : frames) {
            try {
                driver.switchTo().frame(frame);
                if (tryClick(By.cssSelector("button[data-testid*='accept' i], button[id*='accept' i], button[aria-label*='accept' i]"))) {
                    return;
                }
                if (tryClick(By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'accept')]"))) {
                    return;
                }
            } catch (Exception ignored) {
            } finally {
                driver.switchTo().defaultContent();
            }
        }
    }
}
