package com.battleship.pages;

public enum GameResult {

    WIN("Victory"),
    DEFEAT("Defeat - opponent sank all our ships"),
    OPPONENT_LEFT("Opponent disconnected and left the game"),
    TIMEOUT("Game did not finish within the allowed time");

    private final String description;

    GameResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
