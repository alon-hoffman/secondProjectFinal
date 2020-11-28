package TestOps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ClickAndWrite {

    public void writeInElement(By locator, String text) {
        TestUtils.WebDriverInstance
                .getInstance()
                .findElement(locator)
                .sendKeys(text);
    }
}
