// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

public interface PluginLogger {

    default void debug(final String message) {
        log(Level.DEBUG, message);
    }

    default void debug(final String message, final Object... args) {
        log(Level.DEBUG, message, args);
    }

    default void info(final String message) {
        log(Level.INFO, message);
    }

    default void info(final String message, final Object... args) {
        log(Level.INFO, message, args);
    }

    default void warn(final String message) {
        log(Level.WARN, message);
    }

    default void warn(final String message, final Object... args) {
        log(Level.WARN, message, args);
    }

    default void error(final String message) {
        log(Level.ERROR, message);
    }

    default void error(final String message, final Object... args) {
        log(Level.ERROR, message, args);
    }

    void log(final Level level, final String message);

    void log(final Level level, final String message, final Object... args);

    enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
    }
}
