package TestOps;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import TestUtils.WebDriverInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
public class Page3WrittenPersonalInfo {

    public void thisIsMe () {
    //do {
        FindElements FE = new FindElements();
        FE.WaitSomeTime(1000,"In Page3");

        ClickAndWrite clickAndWrite = new ClickAndWrite();
        // <input id="ember1233" placeholder="שם פרטי" required="" type="text" class="ember-view ember-text-field">
        By ByParLink = new By.ByPartialLinkText("תמצאו");

        String userName = "קובי";
        clickAndWrite.writeInElement(By.id("ember1235"), userName);
        if (AssertField(userName,WebDriverInstance.getInstance().findElement(By.id("ember1235")))) {

            String TestEmail = "test" + this.generateRandom() + "." + this.generateRandom() + "@gmail.com";
            clickAndWrite.writeInElement(By.id("ember1237"), TestEmail);

            if (AssertField(TestEmail,WebDriverInstance.getInstance().findElement(By.id("ember1237")))) {

                String UserPassword = "AutoProj"+generateRandom();

                // <input id="valPass" placeholder="סיסמה" required="" type="password" class="ember-view ember-text-field">
                clickAndWrite.writeInElement(By.id("valPass"), UserPassword);
                // <input id="ember1239" placeholder="אימות סיסמה" required="" type="password" data-parsley-equalto="#valPass" data-parsley-equalto-message="הסיסמאות לא זהות, אולי זה מהתרגשות :)" class="ember-view ember-text-field">
                clickAndWrite.writeInElement(By.id("ember1241"), UserPassword);
                System.out.println("UserPassword :: "+UserPassword);
                // Password is protected - returned value does not match

                            try {
                                clickTheBigButton("button");
                            } catch (Exception e) {
                                System.out.println("Exception in Button Click"+e.getMessage());
                            }
            }// endif

        } else {
            System.out.println("Failed Assert Test.");
        }

        FE.WaitSomeTime(2000,"Page3WrittenPersonalInfo.thisIsMe() :: After Loop :: Check URL");

    //} while(WebDriverInstance.getInstance().getCurrentUrl().equals("https://buyme.co.il/?modal=login"));

    }

    private boolean AssertField(String expected, WebElement el) {

        FindElements fe = new FindElements();
        fe.WaitSomeTime(1000, "Wait for Field to update");
        String actual = el.getAttribute("value");

        //actual = actual.replaceAll("\\s+","");
        System.out.println("DBG: Expected="+expected+" :: actual="+actual);


        //String actualToPerson = GetTextFromInputElBelow(InputFieldToPerson);

        if (fe.AssertText(expected,actual)) {
//------------------------------------------------------------------------------------
            System.out.println("-->Passed Assert Test :: actual="+actual);
        }
        else
        {
            System.out.println("Expected="+expected+" :: actual="+actual);
            return false;
        }

        return true;
    }


    private void clickTheBigButton(String LocalTagName)  {

        FindAndClick findAndClick= new FindAndClick();
        By ByLookup = By.tagName(LocalTagName);
        findAndClick.clickElement(ByLookup);

    }

    private int generateRandom () {
        Random rnd = new Random();
        return rnd.nextInt(10000);
    }
}

