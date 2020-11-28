import TestUtils.*;

public class Main {

    public static void main(String[] args)

    {
        XMLsource source = new XMLsource();
        String websiteToTest = source.GetTestURL();
        System.out.println("DBG MSG: websiteToTest = "+websiteToTest);
        TestConductor newTest = new TestConductor(websiteToTest);
        System.out.println("DBG MSG: Finished Setting up Property :: Default is Chrome driver");
        //--------------------------
          newTest.PerformTests(source.GetDriverType());
        //--------------------------
        System.out.println("DBG MSG: Finished Testing "+websiteToTest);
    }
}



