package com.battleship.utils;

import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Locators {

    private Locators() {
    }

    public static By textContains(String... parts) {
        String condition = Arrays.stream(parts).map(p -> "contains(text(),'" + p + "')")
                .collect(Collectors.joining(" or "));
        return By.xpath("//*[" + condition + "]");
    }

    public static By textEquals(String text) {
        return By.xpath("//*[normalize-space(text())='" + text + "']");
    }
}
