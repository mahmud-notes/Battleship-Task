package com.battleship.steps;

import com.battleship.pages.GameResult;
import com.battleship.pages.GamePage;
import com.battleship.pages.SetupPage;
import com.battleship.strategy.Cell;
import com.battleship.strategy.GameStrategy;
import com.battleship.strategy.ShotResult;
import com.battleship.utils.Settings;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

public class GameSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameSteps.class);
    private static final Duration TURN_TIMEOUT = Duration.ofSeconds(10);
    private static final int RANDOM_LIMIT = 15;

    private final SetupPage setupPage = new SetupPage();
    private final GamePage gamePage = new GamePage();
    private final Random random = new Random();

    public void selectRandomOpponent() {
        setupPage.selectRandomOpponent();
    }

    public void arrangeShipsRandomly() {
        int times = 1 + random.nextInt(RANDOM_LIMIT);
        LOGGER.info("Arranging ships randomly, {} click(s)", times);
        setupPage.arrangeRandomly(times);
    }

    public void startGame() {
        setupPage.clickPlay();
    }

    public void waitForOpponent() {
        LOGGER.info("Waiting for opponent up to {} sec", Settings.OPPONENT_WAIT.toSeconds());
        if (!gamePage.waitForGameStart(Settings.OPPONENT_WAIT)) {
            Assert.fail("No opponent connected within " + Settings.OPPONENT_WAIT.toSeconds() + " seconds");
        }
    }

    public GameResult startPlayingGame() {
        GameStrategy strategy = new GameStrategy();
        long deadline = System.currentTimeMillis() + Settings.GAME_TIMEOUT.toMillis();
        int shots = 0;
        gamePage.saveBoardInitialCells();

        while (System.currentTimeMillis() < deadline) {
            Optional<GameResult> outcome = gamePage.getGameOutcome();
            if (outcome.isPresent()) {
                LOGGER.info("Game finished after {} shots: {}", shots, outcome.get());
                return outcome.get();
            }
            if (!gamePage.isMyTurn()) {
                gamePage.waitForMyTurnOrGameEnd(TURN_TIMEOUT);
                continue;
            }

            gamePage.getMarkedCells().forEach(strategy::markUnknownCell);

            Cell target = strategy.nextMove();
            ShotResult result;
            try {
                result = gamePage.shootCell(target);
            } catch (TimeoutException e) {
                strategy.markUnknownCell(target);
                continue;
            }
            shots++;
            strategy.registerResult(target, result);
            LOGGER.info("Shot {} at {} -> {}", shots, target, result);
        }
        return GameResult.TIMEOUT;
    }

}
