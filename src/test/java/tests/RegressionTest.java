package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegressionTest {
    @Test(groups = "regression")
    public void verifyRegressionScenario() {

        System.out.println("Running Regression Test from CI");

        Assert.assertEquals(10 + 10, 20);
    }
}
