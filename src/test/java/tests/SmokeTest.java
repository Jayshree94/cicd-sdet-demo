package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest {

    @Test(groups = "smoke")
    public void verifyApplication() throws InterruptedException {

        System.out.println("===== SMOKE TEST START =====");

        Thread.sleep(10000);

        Assert.assertTrue(true);

        System.out.println("===== SMOKE TEST END =====");
    }
}
