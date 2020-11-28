package TestOps;

import com.aventstack.extentreports.gherkin.model.ScenarioOutline;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FindAndClick {

    public void clickElement(By locator) {
        System.out.print("DBGmsg:: Element = "+locator.toString()+ " :: Displayed = ");
        if (getWebElement(locator).isDisplayed()) {

            System.out.println("yes");

            getWebElement(locator).click();
            System.out.println("DBGmsg: Clicked element "+locator.toString());
        } else {
            System.out.println("no");
        }

    }

    public void ClickByPartialLink(String txtPartialLink){
        By ByPartial = new By.ByPartialLinkText(txtPartialLink);
        WebElement we = getElement(ByPartial);
        we.click();
        System.out.println("Clicked new el: "+we.getTagName()+" Element Text is "+we.getText());
    }


    public WebElement getElement(By locator) {
        return getWebElement(locator);
    }

    private WebElement getWebElement(By locator) {
        return TestUtils.WebDriverInstance.getInstance().findElement(locator);
    }
}

