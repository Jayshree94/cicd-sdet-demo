package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest {

    @Test
    public void verifyApplication() {

        System.out.println("Running Smoke Test from CI");
        Assert.assertTrue(true);

    }
}