package com.battleship.elements;

import org.openqa.selenium.By;

public class RadioButton extends BaseElement {

    public RadioButton(By locator, String name) {
        super(locator, name);
    }

    public void select() {
        click();
    }
}
