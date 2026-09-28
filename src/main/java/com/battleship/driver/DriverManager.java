package com.battleship.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverManager {

    private static WebDriver webDriver;

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        if (webDriver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            webDriver = new ChromeDriver(options);
        }
        return webDriver;
    }

    public static void quit() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
