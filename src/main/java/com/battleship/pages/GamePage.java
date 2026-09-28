package com.battleship.pages;

import com.battleship.driver.DriverManager;
import com.battleship.elements.Label;
import com.battleship.strategy.Cell;
import com.battleship.strategy.ShotResult;
import com.battleship.utils.Locators;
import com.battleship.utils.Settings;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GamePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePage.class);
    private static final int BOARD_SIZE = Settings.BOARD_SIZE;
    private static final int INTERVAL = 100;
    private static final String OUTER_HTML_ATTR = "outerHTML";
    private static final String OUTER_HTML_JS_SCRIPT = "return Array.from(document.querySelectorAll('.battlefield__rival td'))"
            + ".map(function(e) { return e.outerHTML; });";
    private static final Duration TURN_SWITCH_TIMEOUT = Duration.ofSeconds(3);
    private static final By RIVAL_CELLS = By.cssSelector(".battlefield__rival td");

    private final Label myTurn = new Label(Locators.textContains("Ваш ход", "ваш ход"), "My turn status");
    private final Label opponentTurn = new Label(Locators.textContains("ходит"), "Opponent turn status");
    private final Label victory = new Label(Locators.textContains("победили"), "Victory message");
    private final Label defeat = new Label(Locators.textContains("проиграли"), "Defeat message");
    private final Label opponentLeft = new Label(Locators.textContains("покинул"), "Opponent left message");
    private List<String> initialBoardCells;

    public boolean waitForGameStart(Duration timeout) {
        try {
            new WebDriverWait(DriverManager.getDriver(), timeout)
                    .until(d -> isMyTurn() || isOpponentTurn() || getGameOutcome().isPresent());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isMyTurn() {
        return myTurn.isDisplayed();
    }

    public boolean isOpponentTurn() {
        return opponentTurn.isDisplayed();
    }

    private boolean waitForOpponentTurn(Duration timeout) {
        try {
            new WebDriverWait(DriverManager.getDriver(), timeout, Duration.ofMillis(INTERVAL))
                    .until(d -> isOpponentTurn() || getGameOutcome().isPresent());
        } catch (TimeoutException ignored) {
            LOGGER.info("Still our turn");
        }
        return isOpponentTurn();
    }

    public void saveBoardInitialCells() {
        initialBoardCells = readOpponentBoardCells();
    }

    public List<Cell> getMarkedCells() {
        List<Cell> marked = new ArrayList<>();
        List<String> currentBoardCells = readOpponentBoardCells();
        if (initialBoardCells == null || currentBoardCells.size() != initialBoardCells.size()) {
            return marked;
        }
        for (int i = 0; i < currentBoardCells.size(); i++) {
            if (!currentBoardCells.get(i).equals(initialBoardCells.get(i))) {
                marked.add(new Cell(i / BOARD_SIZE, i % BOARD_SIZE));
            }
        }
        return marked;
    }

    @SuppressWarnings("unchecked")
    private List<String> readOpponentBoardCells() {
        return (List<String>) ((JavascriptExecutor) DriverManager.getDriver()).executeScript(OUTER_HTML_JS_SCRIPT);
    }

    public Optional<GameResult> getGameOutcome() {
        if (victory.isDisplayed()) {
            return Optional.of(GameResult.WIN);
        }
        if (defeat.isDisplayed()) {
            return Optional.of(GameResult.DEFEAT);
        }
        if (opponentLeft.isDisplayed()) {
            return Optional.of(GameResult.OPPONENT_LEFT);
        }
        return Optional.empty();
    }

    public ShotResult shootCell(Cell target) {
        List<WebElement> cells = DriverManager.getDriver().findElements(RIVAL_CELLS);
        WebElement cell = cells.get(target.row() * BOARD_SIZE + target.col());

        String before = cell.getAttribute(OUTER_HTML_ATTR);
        cell.click();

        new WebDriverWait(DriverManager.getDriver(), Settings.SHOT_TIMEOUT)
                .until(d -> !before.equals(cell.getAttribute(OUTER_HTML_ATTR)));

        boolean turnPassed = waitForOpponentTurn(TURN_SWITCH_TIMEOUT);
        LOGGER.info("Cell after shot: {} | opponent turn shown: {}", cell.getAttribute(OUTER_HTML_ATTR), turnPassed);
        return turnPassed ? ShotResult.MISS : ShotResult.HIT;
    }

    public void waitForMyTurnOrGameEnd(Duration timeout) {
        try {
            new WebDriverWait(DriverManager.getDriver(), timeout, Duration.ofMillis(INTERVAL))
                    .until(d -> isMyTurn() || getGameOutcome().isPresent());
        } catch (TimeoutException ignored) {
            LOGGER.info("Game Finished");
        }
    }
}