package com.battleship.utils;

import java.time.Duration;

public final class Settings {

    public static final String BASE_URL = "http://ru.battleship-game.org";
    public static final Duration ELEMENT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration SHOT_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration GAME_TIMEOUT = Duration.ofMinutes(20);
    public static final Duration OPPONENT_WAIT = Duration.ofSeconds(180);
    public static final int BOARD_SIZE = 10;

    private Settings() {

    }
}
