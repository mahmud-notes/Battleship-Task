package com.battleship.elements;

import com.battleship.driver.DriverManager;
import com.battleship.utils.Settings;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class BaseElement {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseElement.class);
    protected final By locator;
    protected final String name;

    protected BaseElement(By locator, String name) {
        this.locator = locator;
        this.name = name;
    }

    protected WebDriver webDriver() {
        return DriverManager.getDriver();
    }

    protected WebDriverWait waitFor(Duration timeout) {
        return new WebDriverWait(webDriver(), timeout);
    }

    public void click() {
        LOGGER.info("Click on '{}'", name);
        waitFor(Settings.ELEMENT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator))
                .click();
    }

    public boolean isDisplayed() {
        try {
            return webDriver().findElements(locator).stream().anyMatch(WebElement::isDisplayed);
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

}
