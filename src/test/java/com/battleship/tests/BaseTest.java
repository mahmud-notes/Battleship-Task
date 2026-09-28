package com.battleship.tests;

import com.battleship.driver.DriverManager;
import com.battleship.utils.Settings;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    @BeforeMethod
    public void setUp() {
        DriverManager.getDriver().get(Settings.BASE_URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quit();
    }
}
