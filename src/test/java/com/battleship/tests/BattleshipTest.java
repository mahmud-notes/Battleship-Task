package com.battleship.tests;

import com.battleship.pages.GameResult;
import com.battleship.steps.GameSteps;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BattleshipTest extends BaseTest {

    private final GameSteps gameSteps = new GameSteps();

    @Test()
    public void battleshipGameTestAgainstRandomOpponent() {
        gameSteps.selectRandomOpponent();
        gameSteps.arrangeShipsRandomly();
        gameSteps.startGame();
        gameSteps.waitForOpponent();
        GameResult gameResult = gameSteps.startPlayingGame();
        Assert.assertEquals(gameResult, GameResult.WIN,
                "Game was not won. Reason: " + gameResult.getDescription());
    }
}
