package me.uquark.barrymore.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public final String name;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public enum LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        FATAL
    }

    public Logger(String name) {
        this.name = name;
    }

    public void log(LogLevel level, String message) {
        System.out.printf("%s [%s] [%s] > %s\n", formatter.format(LocalDateTime.now()), name, level.toString(), message);
    }

    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }
}
