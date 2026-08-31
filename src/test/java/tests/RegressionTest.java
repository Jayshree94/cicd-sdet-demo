package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegressionTest {

    @Test(groups = "regression")
    public void verifyRegressionScenario() throws InterruptedException {

        System.out.println("===== REGRESSION TEST START =====");

        Thread.sleep(10000);

        Assert.assertEquals(10 + 10, 20);

        System.out.println("===== REGRESSION TEST END =====");
    }
}
