package TestUtils;

import TestOps.FindElements;
import com.aventstack.extentreports.gherkin.model.ScenarioOutline;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestConductor {

    //"C:\\Users\\OWNER\\Desktop\\chromedriver.exe";
    private String websiteName; // default test site

    public TestConductor (String WebSiteName){
       websiteName = WebSiteName;
    }

  // @BeforeTest
    private boolean setProperty(String name,String path) {
        try {
            System.setProperty(name, path);
            System.out.println("DBG MSG: Finished Setting up Property Successfully in TestConductor.setProperty().");
            return true;
        } catch (Exception e) {
            System.out.println("DBG MGS: Error in TestConductor.setProperty() ::"+e.getMessage());
            return false;
        }
    }

   // @BeforeClass
    private void DriverInstantiation (String SiteName) {
        try {
            // will be instanciated before class operations
            WebDriver driver = WebDriverInstance.getInstance();
            driver.get(SiteName);
            driver.manage().window().maximize();
            System.out.println("DBG MSG: Driver Instantiation Complete.");
        } catch (Exception e) {
            System.out.println("DBG MGS: Unhandled Error in TestConductor.DriverInstantiation() ::"+e.getMessage());
        }
    }// open a given website

    @Test(priority = 1)
    private void test01_register(){
        try {
            TestOps.FirstPage firstPage = new TestOps.FirstPage();
            firstPage.clickToRegister("seperator-link");
            System.out.println("DBG MSG: test01_registered to website ");
        } catch (Exception e) {
            System.out.println("DBG MGS: Unhandled Error in TestConductor.test01_register() ::"+e.getMessage());
        }
    }

    @Test(priority = 2)

    private void test02_register(){
        TestOps.Page2LoginOrRegister page2= new TestOps.Page2LoginOrRegister();
        page2.clickedRegister();
    }

    @Test(priority = 3)

    private void test03_writingInfo(){
        FindElements findElements = new FindElements();
        findElements.WaitSomeTime(2500,"Wait for elements to load - page3");
        TestOps.Page3WrittenPersonalInfo page3= new TestOps.Page3WrittenPersonalInfo();
        page3.thisIsMe();
    }

    @Test(priority = 4)

    private void test04_SelectAndFill(){
        //System.out.println("DBG: Starting Test 4");
        FindElements findElements = new FindElements();
        //findElements.WaitSomeTime(500,"Just to test the wait cycle");
        try {
            findElements.Page4FindBy("active-result");
        } catch (Exception e) {
            System.out.println("Exception in test 04_SelectAndFill() :: "+e.getMessage());
        }
    }

    /// more tests

    @AfterClass

    private void finishTest(){
        try {
            //driver.quit(); //DBG MSG - Driver is closed from object
            System.out.println("Thank you for using our System.");
        }   catch (Exception e) {
            System.out.println("Error happened :: "+e.getMessage());
        }
    }

    public void PerformTests(String DriverType)
    {

     if (DriverType.equals("Chrome")) { // set property by driver type

         String driverFileName = "chromedriver.exe";
         String localDriverPath = "C:\\Users\\MD\\Desktop\\Alon Java Projs\\SecondProject\\SecondProject\\src\\";
         String driverPath = localDriverPath + driverFileName;
         //selection of curr driver
         String webDriverName = "webdriver.chrome.driver";

         if (setProperty(webDriverName, driverPath)) {
            //@BeforeClass
            DriverInstantiation(websiteName);
            //@Test(priority = 1)
            test01_register();
            //@Test(priority = 2)
            test02_register();
            //@Test(priority = 3)
            test03_writingInfo();
            //@Test(priority = 4)
            test04_SelectAndFill();

        } else {
            System.out.println("Error in property setup. Test Aborted.");
            }

         finishTest();// end if true

        } else {
         System.out.println("Unrecognized Driver type. Cannot perform the Tests.");
        }
     }
}
