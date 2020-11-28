package TestOps;

import TestUtils.WebDriverInstance;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.openqa.selenium.support.locators.RelativeLocator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FindElements {
//------------------------------------------------------------------------------------------------------------------------
//  Global Vars
//------------------------------------------------------------------------------------------------------------------------
    private WebDriver driver;
    private String CurrURL;
//------------------------------------------------------------------------------------------------------------------------
//  Main Procedure block :: Called from Main Conductor
//------------------------------------------------------------------------------------------------------------------------

    public void Page4FindBy (String byTextClassName)  {

        final String startingURL = WebDriverInstance.getInstance().getCurrentUrl();
        final int NumOfTests = 10;
    //---------------------------------------------
    // Main Test Repeat loop
    //---------------------------------------------
        int repeatTest = 0;
        while (repeatTest < NumOfTests) {
            repeatTest++;

            //------------Category Selection-------------------------------------------------------
            if (SelectFromCategories(startingURL,byTextClassName,repeatTest,NumOfTests)) {

                //----------------------------------------------
                // select one random choice from found list
                //----------------------------------------------

                WaitSomeTime(3000, "moved to new page :");
                CurrURL = driver.getCurrentUrl(); // driver navigation is 100%, assert not required
                System.out.println("moved to new page :: " + CurrURL);

                //--------------------------------------------------------------------------------------------------
                // Start BUNDLE TEST HERE :: Check next Page type :: bundle ,package ,supplier or cancel next
                //--------------------------------------------------------------------------------------------------
                boolean continueRepeatLoop = true;
                boolean continueCardSelection = false;

                // DBG: Report the next type of page
                ReportMajorActivity("Testing the next page type.");
                ReportBundleTest(true);

                // nestedIF to determine the type of page in the next step.
                if ((CurrURL.contains("bundle")) || CurrURL.contains("package")) {
                    ReportMajorActivity("-->Expected Next page is : Bundle or package :: Skipping Card Selection.");
                } else {
                    if (CurrURL.contains("supplier")) {
                        ReportMajorActivity("-->Selecting A Card from Cards list :: expected next page is : supplier.");
                        continueCardSelection = true;
                    } else {
                        ReportMajorActivity("-->Cannot locate next step Items :: Must Goto end of RepeatLoop.");
                        continueRepeatLoop = false;
                    }
                }// end if-else bundle

                //--------------------------------------------------------------------------------------------------
                // conditional continue - depends on the page type && if list was found
                //--------------------------------------------------------------------------------------------------
                if (continueRepeatLoop) {
                    if (continueCardSelection) {
                        // ----------------------------------
                        selectACardUntilFound();
                        // ----------------------------------
                    }// end if cardSelection
                        //-----------------------------------
                        DoFinalPageSelections();
                        //-----------------------------------
                }// end if ContinueRepeatLoop
                //scrollDownToView();
                //----------------------------------------------------------------------------
                if (repeatTest < NumOfTests) {
                    WaitSomeTime(4000, "Test " + repeatTest + " Complete! Wait before next Test, out of " + NumOfTests);
                } // end if
                //----------------------------------------------------------------------------

            } else {
                ReportMajorActivity("Some Error happened during Selection from Categories.");
            } // end if category selection complete

            //----------------------------------------------------------------------------
            //                ReportMajorActivity("DBG:: -->Press Any Key to Continue....");
            //                int inputData = System.in.read();
            //----------------------------------------------------------------------------

        }// end repeat tests - first while loop

        //----------------------------------------------------------------------------
        // Finally DO:
        ReportMajorActivity("      --- End Test Sequence ---");
        WaitSomeTime(5000, "Test "+repeatTest+" Complete! Finished "+repeatTest+"Tests, out of "+NumOfTests);
        driver.close();
        ReportMajorActivity("Driver Closed successfully");
        //----------------------------------------------------------------------------
    }// end Page4 find by

    public boolean AssertText (String Expected, String Actual) {

        try {
            Assert.assertEquals(Expected, Actual);
            ReportMajorActivity("Assert.equals "+Expected+"=="+Actual+" :: true");
            return true;

        } catch (Exception e) {
            ReportMajorActivity("Error in Assert Text :: "+e.getMessage());
            return false;
        }
    }

    public void WaitSomeTime(int milsec, String reason) {
        //Actions action = new Actions(driver);

        try {
            Thread.sleep(milsec);
            System.out.println("Waiting for " + milsec + "ml wait time :: " + reason);
            //return true;
        } catch (InterruptedException e) {
            System.out.println("Error waiting");
            e.printStackTrace();
            //return false;
        }
    }
//------------------------------------------------------------------------------------------------------------------------
//  Helper Methods
//------------------------------------------------------------------------------------------------------------------------

    private boolean SelectFromCategories (String StartingURL, String ByTextClassName, int repeatTest, int NumOfTests) {
        int foundListSize;
        List<WebElement> insideDivElements;

        do {
            NavigateDriverTo(StartingURL);
            ReportMajorActivity("Starting Test "+repeatTest+" out of "+NumOfTests);

            // CategorySelection
            int budget = SelectAndClickMenuItem("סכום", ByTextClassName);
            int region = SelectAndClickMenuItem("אזור", ByTextClassName);
            int category = SelectAndClickMenuItem("קטגוריה", ByTextClassName);

            StringBuilder newURL = new StringBuilder(); // build the new URL for Navigation after the search
            newURL.append("https://buyme.co.il/search?budget=").append(budget)
                    .append("&category=").append(category)
                    .append("&region=").append(region);
            System.out.println("Default URL to navigate to :" + newURL.toString());

            // Click The Button

            By ByParLink = new By.ByPartialLinkText("תמצאו");

            if(WaitAndClickElement(WebDriverInstance.getInstance().findElement(ByParLink))) {
                System.out.println("WaitAndClickElement ::"+ByParLink.toString()+" Clicked the Category Button");
            }
            else {
                System.out.println("Error Clicking Category Button  ---> Cannot Continue Tests. Reset required.");
                return false; // end loop here
                //break; // end Do loop - Handled issue
                //////------------------------------------------------------------------
            }// If Button Clicked  - continue

            System.out.println("Driver Navigation Complete :: Current URL :: " + ReportDriverNavigation());
            ReportMajorActivity("Page 4 Complete -> reading results list before next step");

            // ---------------------------------------------------------------------------------------
            // Page 5 - Get Cards list = may be empty -> repeat random selection until list has values
            // ---------------------------------------------------------------------------------------

            try {
                driver = TestUtils.WebDriverInstance.getInstance();
                CurrURL = driver.getCurrentUrl();
                //      Assert.assertEquals(CurrURL, driver.getCurrentUrl()); // did the driver navigate?
                //      System.out.println("Current URL Passed Assert Test : " + CurrURL);
            } catch (Exception e) {
                //e.printStackTrace();
                //ReportMajorActivity("AssertFailed :: " + e.getMessage());

                System.out.println("Reactivating the driver with the selected choices.");
                NavigateDriverTo(newURL.toString());

                System.out.println("driver.getCurrentUrl() " + driver.getCurrentUrl());

                ClickMenuItem("סכום", ByTextClassName, budget);
                ClickMenuItem("אזור", ByTextClassName, region);
                ClickMenuItem("קטגוריה", ByTextClassName, category);
                WebDriverInstance.getInstance().findElement(ByParLink).click(); // Click The Button again!!!!!
                // wait until all elements show on screen

            } // driver have been reactivated with the new values, navigation continues


            WaitSomeTime(3000, "Waiting for results to show on screen");
            ReportMajorActivity("Clicked the button to get the search results on screen :: " + ByParLink.toString());

            //------------------------------------------------------------------------------------------
            // continue test to select one option from found list if current page is not a bundle
            //------------------------------------------------------------------------------------------

            By BySubMethod = By.className("card-items");
            By Tag_a = new By.ByTagName("a");
            try {
                WebElement DivCardItems = driver.findElement(BySubMethod);      // (new By.ByClassName("card-items"));// driver.findElements(ByMethod); // get all Divs
                ReportMajorActivity("Checking Found Div Element for Links :: " + DivCardItems.getTagName());

                System.out.println("" + DivCardItems.getText());

                ReportMajorActivity("Getting Links List -> reading results after URL update");
                //scrollDownToView(DivCardItems);

                //page-title

                WaitSomeTime(1000,"Before Scroll Down");
                WebElement PageTitle = driver.findElement(By.className("page-title"));

                ReportMajorActivity("Scrolling down Page "+PageTitle.getText());
                scrollDownToView(DivCardItems);
                scrollDownToView(DivCardItems);
                scrollDownToView();

                WaitSomeTime(2000,"After Scroll Down");

                List<WebElement> testList = DivCardItems.findElements(By.className("thumbnail"));
                ReportMajorActivity("Test List Size is "+testList.size());
                System.out.println("--> PageTitle :: "+ PageTitle.getText());
                insideDivElements = findElementsIn(DivCardItems, Tag_a);
                foundListSize = insideDivElements.size();

                ReportMajorActivity("Size :: List of found links ::" + insideDivElements.size());
                if (foundListSize>0)
                {
                    int randSelection = generateRandom(0, insideDivElements.size());
                    System.out.println("Selected Item #" + randSelection + " text::" + insideDivElements.get(randSelection).getText() + " -->TagName::" + insideDivElements.get(randSelection).getTagName());
                    //----------------------
                    //WaitSomeTime(100, "avoiding Error :: element is not attached...");
                    WaitAndClickElement(insideDivElements.get(randSelection));//.click();
                    ReportMajorActivity("Clicked Selected Item #" + randSelection + " " + insideDivElements.get(randSelection).getText());
                }

            } catch (Exception e) {
                System.out.println("Objects were not found :: "+e.getMessage());
                foundListSize = 0;
                WaitSomeTime(1000,"Wait to restart the loop :: REASON: list is empty");
            }
        } while (foundListSize == 0);  // repeat action until found list has elements

        return true;
    } // end SelectFromCategories

    private void NavigateDriverTo(String url) {
        driver= WebDriverInstance.getInstance();
        String BeforeURL = driver.getCurrentUrl();
        driver.get(url);
        driver.navigate();
        //System.out.println("Driver Navigated to :: "+ReportDriverNavigation());
        //System.out.println("Driver Navigation Complete :: Current URL :: " + ReportDriverNavigation(BeforeURL));
        WaitSomeTime(4000, "Active URL :: "+url);
    }

    private String ReportDriverNavigation(String beforeURL){
        String afterURL = beforeURL;
        System.out.println("--> TestUtils.WebDriverInstance.getInstance() BEFORE :: " + beforeURL);
        do {
            //System.out.println("--> TestUtils.WebDriverInstance.getInstance()" + afterURL);
            WaitSomeTime(100, "Waiting for URL update...");
            afterURL = WebDriverInstance.getInstance().getCurrentUrl();
        } while (beforeURL.equals(afterURL));

        return afterURL;
    }
    private String ReportDriverNavigation(){
        return ReportDriverNavigation(WebDriverInstance.getInstance().getCurrentUrl());
    }

    private void selectACardUntilFound () {
        // ----------------------------------
        // select a card loop until found
        // ----------------------------------
        boolean RepeatSelectACard;
        do {

            driver = TestUtils.WebDriverInstance.getInstance();
            CurrURL = driver.getCurrentUrl();
            // Tried to Scroll down to bottom of the page so all elements will be visible
            // ----------> found inconsistency between found elements and reported list by the site.
            //scrollDownToView();
            WebElement CardsEl = driver.findElement(new By.ByClassName("card"));
            scrollDownToView(CardsEl);
            scrollDownToView(CardsEl);
            scrollDownToView();
            //-------------------------------------------------------------------------------------------
            ReportMajorActivity("select a Card, Click Selected");
            WaitSomeTime(2000, "Wait for scroll down");
            //-------------------------------------------------------------------------------------------

            List<WebElement> cards = driver.findElements(new By.ByClassName("card"));
            System.out.println("Cards List Size :: " + cards.size());

            //-------------------------------------------------------------------------------------------
            // Find the manual gift card
            //-------------------------------------------------------------------------------------------
            WebElement relevantBtn = null;

            int CardsIndex = 0;
            int foundCard = -1;

            // checks first 6 links
            for (WebElement card : cards) {

                WebElement Btn = card.findElement(new By.ByClassName("btn")); //(new By.ByPartialLinkText("לבחירה"));
                System.out.println("Button Text : " + Btn.getText() + " :: TagName : " + Btn.getTagName());

                if (Btn.getTagName().startsWith("button")) {

                    foundCard = CardsIndex;
                    relevantBtn = Btn;
                }
                CardsIndex++;
            }// end foreach loop
            //-----------------------------------------------------------------------------------------------

            ReportMajorActivity("Found " + CardsIndex + " Items.");
            RepeatSelectACard = false;

            if (foundCard < 0) {
                ReportMajorActivity("Manual Card was not found. Selecting a random card instead.");
                // choose random card and press select
                relevantBtn = cards.get(generateRandom(0, cards.size())).findElement(new By.ByTagName("a"));

            } else {

                ReportMajorActivity("Manual Gift Card is item #" + foundCard);

                WebElement inputField = cards.get(foundCard).findElement(new By.ByTagName("input"));
                int sum = generateRandom(1, 5) * 100;
                inputField.sendKeys(String.valueOf(sum));
                System.out.println("Sent Keys to inputField in Cards sum=" + sum);
            }

            if (!WaitAndClickElement(relevantBtn)) {
                ReportMajorActivity("Error in Cards.card.relevantButton " + relevantBtn.getTagName());
                System.out.println("Try A Different Card - RepeatSelectACard = true;");
                RepeatSelectACard = true;
            }
        } while (RepeatSelectACard);
        ReportMajorActivity("Finished Card Selection - starting Final Page Selections.");
    }// end card selection

    private void DoFinalPageSelections () {
        //----------------------------------------------------------------------------
        WaitSomeTime(3000, "moved to new page - accept User information");
        //----------------------------------------------------------------------------
        driver = TestUtils.WebDriverInstance.getInstance();
        CurrURL = driver.getCurrentUrl(); // driver navigation is 100%, assert not required
        ReportMajorActivity("driver navigation  :: "+CurrURL);
        WebElement title = driver.findElement(new By.ByClassName("page-title"));
        System.out.println("Current Page Title is :: "+title.getText());

        //----------------------------------------------------------------------------------------------------
        // make final page selections
        //----------------------------------------------------------------------------------------------------
        // radio Button - someone else    .ui-radio
        WebElement RadioBtn1 = driver.findElement(new By.ByClassName("selected"));
        System.out.println("Radio Button Selection is :" + RadioBtn1.getText());
        RadioBtn1.click();

        // enter receiver name
        String to = "Mr To Guy";

        WebElement InputFieldToPerson = driver.findElement(RelativeLocator.withTagName("input").below(RadioBtn1));
        InputFieldToPerson.sendKeys(to);

        WaitSomeTime(2000,"Wait for Name Field Update");
        String actualToPerson = InputFieldToPerson.getAttribute("value");
        //String actualToPerson = GetTextFromInputElBelow(InputFieldToPerson);

        if (AssertText(to,actualToPerson)) {
//                    //------------------------------------------------------------------------------------
            System.out.println("-->Passed Assert Receiver Name Test");
        }
        else
        {
            ReportMajorActivity("Expected="+to+"  :: actual="+actualToPerson);
        }

        WebElement inputFrom = driver.findElement(RelativeLocator.withTagName("input").below(InputFieldToPerson));
        //String fromName = GetTextFromInputElBelow(inputFrom);
        String fromName = inputFrom.getAttribute("value");
        //ReportMajorActivity("Found Text in input field From :: "+fromName);
        fromName = fromName.replaceAll("\\s+","");
        String ExpectedName = "קובי";

        if (AssertText(ExpectedName,fromName)) {
            //------------------------------------------------------------------------------------
            System.out.println("-->Passed Assert Sender Name Test");
        }
        else
        {
            ReportMajorActivity("Expected=קובי  but actual="+fromName);
        }

        // pick event from drop down.

        // ClickMenuItem("לאיזה אירוע?", "active-result", 1);
        WebElement EventMenu = WebDriverInstance.getInstance().findElement(new By.ByPartialLinkText("לאיזה אירוע?"));
        EventMenu.click();
        System.out.println(EventMenu.getText());
        System.out.println("Clicked on Drop Down Event");

        //List<WebElement> EventMenuItems = findElementsIn(EventMenu,new By.ByClassName("eventsTypesItems"));
        List<WebElement> EventMenuItems = driver.findElements(By.className("active-result"));
        int menuItemIndex = 0;

        List<Integer> MenuOptions = new ArrayList<>();

        for (WebElement menuItem : EventMenuItems) {
            // System.out.println(menuItemIndex+" MenuItem text "+menuItem.getText());
            if (menuItem.getText().startsWith("יום הולדת")) {
                MenuOptions.add(menuItemIndex);
                System.out.println("birthday item is #" + menuItemIndex);

            } else {
                if (menuItem.getText().startsWith("חתונה")) {
                    System.out.println("Wedding Item is #" + menuItemIndex);
                    MenuOptions.add(menuItemIndex);
                } // end inner if
            } // end else
            menuItemIndex++;
        }

        WaitSomeTime(500, "wait BEFORE click for menu items to update ::");
        EventMenuItems.get(MenuOptions.get(generateRandom(0, MenuOptions.size()))).click();
        WaitSomeTime(500, "wait After click for menu items to update ::");

        WebElement upload = driver.findElement(By.name("fileUpload"));

        // https://buyme.co.il/files/moneyVoucherImage3657311.jpg?1603787839

        //String imgPathOnline = src.substring(0, jpgLocation);
        String imgPathLocal = "C:\\Users\\MD\\Pictures\\Saved Pictures\\pretty sun – חיפוש Google_files\\29cb9550cf608f571fffc5487292931b.jpg";

        WaitSomeTime(1000, "waiting for image Element");

        upload.sendKeys(imgPathLocal); // UPDATE VAR TO YOUR LOCAL IMAGE PATH
        //ButtonRowButtons
        // ember-view sending-methods ui-card relative error-newline
        // dib relative send-options-container

        WebElement dib = WebDriverInstance.getInstance().findElement(new By.ByClassName("sending-methods"));
        System.out.println(dib.getTagName()+ " ::  Dib element.getText() :: "+dib.getText());
        //pick email/ sms
        List<WebElement> dibButtons = dib.findElements(new By.ByTagName("button"));
        System.out.println("dibButtons List Size is :: "+dibButtons.size());

        int email = 0;
        int sms = 0;
        int SelectionIndex = 0;
        for (WebElement btn : dibButtons) {
            //    System.out.println(btn.getText());
            if (btn.getText().startsWith("במייל"))
            {
                email=SelectionIndex;

            } else if (btn.getText().startsWith("ב-SMS"))
            {
                sms=SelectionIndex;
            }
            // end if

            SelectionIndex++;
        } // end for each loop - Select random

        int randSelection = generateRandom(0,2);
        //int randSelection = 1;

        // Click Randomly sms/email
        // List<WebElement> btnsBelow; // used to find the Save Button below input fields

        if (randSelection == 0) //email:: randSelection == 0
        {
            ReportMajorActivity("do email :: Button Text is ::"+dibButtons.get(email).getText());
            dibButtons.get(email).click();
            WaitSomeTime(2500, "Wait for other fields to show");

            WebElement inputEmail = driver.findElement(By.cssSelector("input[type=email]")); // By.ById("ember2042"));
            inputEmail.sendKeys("email@email.com");
            //WaitSomeTime(1000,"Wait for Email to update");
            FindClickSaveButtonBellow(inputEmail);

        } else {//sms :: randSelection == 1

            ReportMajorActivity("do sms :: Button Text is ::"+dibButtons.get(sms).getText());
            dibButtons.get(sms).click();
            WaitSomeTime(2500, "Wait for Tel Input fields to show");
            ///.sendKeys("sms 054-12345678");
            //WebElement SendelTel = driver.findElement(new By.ByCssSelector());
            List<WebElement> Tel = WebDriverInstance.getInstance().findElements(By.cssSelector("input[type=tel]"));
            System.out.println("Tel Fields List Size "+Tel.size());
            // field 1 :::: <input id="ember2054" placeholder="ספרות בלבד, בלי מקף" required="" type="tel" data-parsley-mobile="mobile" data-parsley-group="sms" class="form-control input-theme ember-view ember-text-field">

            WaitSomeTime(1300,"Wait until Tels Field(0) to update");
            Tel.get(0).sendKeys("0541234567");
            // field 2 ::: <input id="resendReciver" placeholder="ספרות בלבד, בלי מקף" required="" type="tel" data-parsley-mobile="mobile" data-parsley-group="sms" class="form-control input-theme   ember-view ember-text-field">
            WaitSomeTime(1300,"Wait until Tels Field(1) to update");
            Tel.get(1).sendKeys("0547654321");

            // WaitSomeTime(1000,"Wait until Tels Fields to update");

            FindClickSaveButtonBellow(Tel.get(0));
        } // end else do sms
        ReportMajorActivity("Final Selections Completed.");
    }// end finalPageSelections

    private void ReportBundleTest (boolean show) {
        if (show) {
            System.out.println("Search index of bundle   : " + CurrURL.indexOf("bundle"));
            System.out.println("Search index of supplier : " + CurrURL.indexOf("supplier"));
            System.out.println("Search index of package  : " + CurrURL.indexOf("package"));
        }
    }

    private int generateRandom (int min, int max) {
        Random rnd = new Random();
        return (rnd.nextInt(max-min)+min);
    }

    private void ReportMajorActivity (String text) {
        System.out.println("_______________________________________________________");
        System.out.println(text);
        System.out.println("_______________________________________________________");
    }

    private void scrollDownToView() {
        driver = TestUtils.WebDriverInstance.getInstance();
        CurrURL = driver.getCurrentUrl();
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        String scrollToBottom = "window.scrollBy(0,document.body.scrollHeight)";
        jse.executeScript(scrollToBottom);
    }

    private void scrollDownToView(WebElement element) {
        driver = TestUtils.WebDriverInstance.getInstance();
        CurrURL = driver.getCurrentUrl();
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        String scrollViewElements = "arguments[0].scrollIntoView();";
        jse.executeScript(scrollViewElements,element);
    }

    private int SelectAndClickMenuItem(String partialLink, String ClassNameToFind) {
        FindAndClick fc = new FindAndClick();
        fc.ClickByPartialLink(partialLink);
        // "active-result"
        int SizeOfSum = WebDriverInstance.getInstance().findElements(By.className(ClassNameToFind)).size();

        System.out.println("Sizeof element list is "+SizeOfSum);
        int ItemSelection = this.generateRandom(1,SizeOfSum);

//        if (partialLink.startsWith("קטגוריה")) {
//
//            ItemSelection = 7; // BUNDLE CASE DEBUG
//        }

        WebElement SelectedEl= WebDriverInstance.getInstance().findElements(By.className(ClassNameToFind)).get(ItemSelection);

        System.out.println("SELECTED ITEM => "+ItemSelection+" "+SelectedEl.getText()); //" Element Text is "+we.getText()
        // WaitSomeTime(500,"Wait for selection to update");
        WaitAndClickElement(SelectedEl);//.click();
        return ItemSelection;
    }

    private boolean WaitAndClickElement (WebElement el) {
        boolean WaitForButton;
        int tries = 5;
        int ActualAttempts = 0;
        int addedTime = 1000;
        do {
            // if error repeats - exit process and go to start?
            try {
                String msg = "Trying to Click :: "+el.getTagName()+" :: Attempt "+ActualAttempts+" out of "+tries;
                WaitSomeTime(addedTime, msg);
                el.click();
                WaitForButton=false;
            }catch (Exception e) {
                ReportMajorActivity("Error in Cards Selected Button :: "+e.getMessage());
                WaitForButton=true;
                addedTime+=1000;
                ActualAttempts++;
            }

        } while (WaitForButton && ActualAttempts<tries);

        if (WaitForButton) { ReportMajorActivity("Error in Cards.card.relevantButton "+el.getTagName());
            return false;
        }
        return true;
    }

    private void ClickMenuItem(String partialLink, String ClassNameToFind, int index) {
        FindAndClick fc = new FindAndClick();
        fc.ClickByPartialLink(partialLink);
        int SizeOfSum = WebDriverInstance.getInstance().findElements(By.className(ClassNameToFind)).size();
        System.out.println("List Sizeof element "+partialLink+" is "+SizeOfSum);
        // ItemSelection = index;
        WebElement SelectedEl= WebDriverInstance.getInstance().findElements(By.className(ClassNameToFind)).get(index);
        ReportMajorActivity("SELECTED ITEM => "+index+" "+SelectedEl.getText()); //" Element Text is "+we.getText()
        SelectedEl.click();
    }

    private List<WebElement> findElementsIn(WebElement TempDriver,By byMethod) {
        return TempDriver.findElements(byMethod); // returns web elements
    }

    private void FindClickSaveButtonBellow (WebElement el){

        List<WebElement> btnsBelow = WebDriverInstance.getInstance().findElements(RelativeLocator.withTagName("button").below(el));

        WaitSomeTime(1000,"Avoiding e=stale element reference:: Below WE="+el.getTagName());
        for(int i=0;i<btnsBelow.size();i++){ // find the save button
            System.out.println(btnsBelow.get(i).getText());
            if (btnsBelow.get(i).getText().contains("שמירה")) {
                // DBG ERROR STALE ELEMENT

                btnsBelow.get(i).click();
                ReportMajorActivity("-->Clicked Save Button (index "+i+") :: ");
                break;
            } // end if
        }//
    }// end find

//------------------------------------------------------------------------------------------------------------------------
//  Work In progress
//------------------------------------------------------------------------------------------------------------------------

    private WebElement GetChosenEl_FromListEl(String tagName,String textToCheck) {
        // check if a webelement has a few organs
        // Iterate the list and return one found element
        driver = TestUtils.WebDriverInstance.getInstance();
        CurrURL=driver.getCurrentUrl();
        // TagName = "form";//"p";
        By ByMethod = new By.ByTagName(tagName);
        List<WebElement> elements = driver.findElements(ByMethod); // get all Divs
        //findElementsIn(TestUtils.WebDriverInstance.getInstance().findElement(By.tagName(TagName)),By.tagName(TagName));//
        System.out.println("GetAll "+tagName+"s ::: List Size : "+elements.size());

        // find specific form element inside list of forms
        int i = 0;
        int found = 0;
        String text="";

        for (WebElement element : elements) {
            System.out.println("Iteration : "+i+" .getLocation() "+element.getLocation()); //+" ::.getText(): "+element.getText()); //" + element.getText());//.hashCode: "+element.hashCode()+"
            text = element.getText();
            //String textToCheck = "סכום";
            if (text.startsWith(textToCheck)) {
                System.out.println("Element "+element.getTagName()+i+" -> Starts with -> "+textToCheck);
                System.out.println("Copied Text : "+text);
                found = i;

            } else {text="";}
            i++;
        }
        // found the right form at location index =1
        System.out.println("elements.get("+found+").getText() : "+elements.get(found).getText()); // Currect info i showing
        return elements.get(found);
    }

    private void getList_ByID(String id_element, int list_index_to_click) {
        try {
            System.out.println("----------------------------Inside GetList_ByID ->"+id_element);
            WebElement web_el = TestUtils.WebDriverInstance.getInstance().findElement(By.id(id_element));

            List<WebElement> els_Inside = findElementsIn(web_el,By.id(id_element)); // Check if element is actually an array of other elements
            String reply = "";
            if (els_Inside.size()>0) {reply = "Size="+ els_Inside.size();}
            else {reply = "Size is 0 - Did it read the element???";}

            System.out.println("Reply : "+reply);
            System.out.println("Found element :: TagName->"+web_el.getTagName()+" ::: Text->"+web_el.getText());

            Actions action = new Actions(driver);

            System.out.println("------getList_ByID:-----Trying to activate the element inside try catch-----------------");
            try {
                Thread.sleep(2000);
                action.moveToElement(web_el).click().build().perform();
                System.out.println("Clicked the element Using Action Command after 2000ml wait time");
            } catch (InterruptedException e) {
                System.out.println("Error Trying to click the element "+web_el.getTagName());
                e.printStackTrace();
            }

            System.out.println("After 1st try Catch expression :: trying to perform Select from list given from element "+web_el.getTagName());

            Select select = new Select(web_el);
            List<WebElement> option = select.getOptions();
            System.out.println("Option Selected : "+option.get(list_index_to_click).getText());
            // select.selectByIndex(list_index_to_click);
            //option.get(list_index_to_click).click();
            System.out.println("Inside try - GetList_ByID : no errors - except object does not provide info");

        } catch (Exception e) {
            //errors
            System.out.println("Error Catch - Inside GetList_ByID : "+id_element+"->"+e.getLocalizedMessage());
            //e.printStackTrace();
        }
        System.out.println("-----------------------Finished GetListByID ->"+id_element+"--------------------------------");
    }

    private void ClickAndReport (WebElement el) {

        Actions action = new Actions(driver);
        action.moveToElement(el).click().build().perform();
        boolean dbgmsg = true;
        if (dbgmsg) {
            System.out.println("________ Click&Report ________");
            //System.out.println("Performed .click().build().perform()");
            System.out.println("el.TagName() ->" + el.getTagName());
            System.out.println("Element getText() :");
            System.out.println("______________________________");
            System.out.println(el.getText());
            System.out.println("______end Click&Report________");
        }
    }

    private String GetTextFromInputElBelow(WebElement el) {
        WebElement input = driver.findElement(RelativeLocator.withTagName("input").below(el));
        String tempValue = input.getAttribute("value");
        return tempValue.replaceAll("\\s+","");
    }

    private String Page4Step1ClickAllAndGetURL (String txtClassNameToClick) {
        // String ByTextClassName = "active-result";
        int budget = SelectAndClickMenuItem("סכום",txtClassNameToClick);
        int region = SelectAndClickMenuItem("אזור",txtClassNameToClick);
        int category = SelectAndClickMenuItem("קטגוריה",txtClassNameToClick);

        // DEBUG BUNDLE CASE

        // https://buyme.co.il/search?budget=4&category=6&region=11
        StringBuilder newURL = new StringBuilder();
        newURL.append("https://buyme.co.il/search?budget=").append(budget)
                .append("&category=").append(category)
                .append("&region=").append(region);
        System.out.println("New URL to navigate to :"+newURL.toString());
        //By ByID = new By.ById("ember838");
        By ByParLink = new By.ByPartialLinkText("תמצאו");
        //<a id="ember838" href="https://buyme.co.il/search?budget=1&amp;category=22&amp;region=2835" rel="nofollow" class="ui-btn search ember-view">                תמצאו לי מתנה
        //</a>

        WebDriverInstance.getInstance().findElement(ByParLink).click(); // Click The Button

        return newURL.toString();
    }

    private void CategorySelection(){

    }
} // end class FindElements
