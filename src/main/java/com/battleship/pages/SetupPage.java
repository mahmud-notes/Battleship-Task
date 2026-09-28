package com.battleship.pages;

import com.battleship.elements.Button;
import com.battleship.elements.RadioButton;
import com.battleship.utils.Locators;

public class SetupPage {

    private final RadioButton randomOpponent =
            new RadioButton(Locators.textEquals("случайный"), "Random opponent");
    private final Button randomlyButton =
            new Button(Locators.textEquals("Случайным образом"), "Randomly");
    private final Button playButton =
            new Button(Locators.textEquals("Играть"), "Play");

    public void selectRandomOpponent() {
        randomOpponent.select();
    }

    public void arrangeRandomly(int times) {
        randomlyButton.clickButtonMultipleTimes(times);
    }

    public void clickPlay() {
        playButton.click();
    }
}
