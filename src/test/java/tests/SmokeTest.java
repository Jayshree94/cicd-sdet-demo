package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest {
    @Test(groups = "smoke")
    public void verifyApplication() {

        System.out.println("Running Smoke Test from CI example");
        Assert.assertTrue(true);

    }
}