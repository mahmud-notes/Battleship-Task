package com.battleship.elements;

import org.openqa.selenium.By;

public class Button extends BaseElement {

    public Button(By locator, String name) {
        super(locator, name);
    }

    public void clickButtonMultipleTimes(int clickCount) {
        for (int i = 0; i < clickCount; i++) {
            click();
        }
    }
}
