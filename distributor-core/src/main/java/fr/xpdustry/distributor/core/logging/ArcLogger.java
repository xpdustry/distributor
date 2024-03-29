/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.core.logging;

import arc.util.ColorCodes;
import arc.util.Log;
import arc.util.Log.LogLevel;
import java.io.Serial;
import java.util.Arrays;
import mindustry.net.Administration;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

public final class ArcLogger extends AbstractLogger {

    @Serial
    private static final long serialVersionUID = 3476499937056865545L;

    private static final Object WRITE_LOCK = new Object();
    private static final Administration.Config TRACE =
            new Administration.Config("trace", "Enable trace logging when debug is enabled.", false);

    private final @Nullable String plugin;

    public ArcLogger(final String name, final @Nullable String plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.isArcLogLevelAtLeast(Log.LogLevel.debug) && TRACE.bool();
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return this.isArcLogLevelAtLeast(Log.LogLevel.debug) && TRACE.bool();
    }

    @Override
    public boolean isDebugEnabled() {
        return this.isArcLogLevelAtLeast(Log.LogLevel.debug);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return this.isArcLogLevelAtLeast(Log.LogLevel.debug);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.isArcLogLevelAtLeast(Log.LogLevel.info);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return this.isArcLogLevelAtLeast(Log.LogLevel.info);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.isArcLogLevelAtLeast(Log.LogLevel.warn);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return this.isArcLogLevelAtLeast(Log.LogLevel.warn);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.isArcLogLevelAtLeast(Log.LogLevel.err);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return this.isArcLogLevelAtLeast(Log.LogLevel.err);
    }

    @Override
    protected @Nullable String getFullyQualifiedCallerName() {
        return null;
    }

    @Override
    protected void handleNormalizedLoggingCall(
            final Level level,
            final @Nullable Marker marker,
            final String messagePattern,
            @Nullable Object @Nullable [] arguments,
            @Nullable Throwable throwable) {
        final var builder = new StringBuilder();

        if (!this.name.equals(ROOT_LOGGER_NAME)) {
            if (this.plugin != null) {
                builder.append(this.getColorCode(level))
                        .append('[')
                        .append(this.plugin)
                        .append(']')
                        .append(ColorCodes.reset)
                        .append(' ');
            }
            builder.append(this.getColorCode(level))
                    .append('[')
                    .append(this.name)
                    .append(']')
                    .append(ColorCodes.reset)
                    .append(' ');
        }

        if (level == Level.ERROR) {
            builder.append(this.getColorCode(level));
        }

        if (throwable == null
                && arguments != null
                && arguments.length != 0
                && arguments[arguments.length - 1] instanceof final Throwable last) {
            throwable = last;
            arguments = arguments.length == 1 ? null : Arrays.copyOf(arguments, arguments.length - 1);
        }

        final var string = builder.append(
                        MessageFormatter.basicArrayFormat(messagePattern.replace("{}", "&fb&lb{}&fr"), arguments))
                .toString();

        synchronized (WRITE_LOCK) {
            if (throwable != null && (arguments == null || arguments.length == 0)) {
                Log.err(string);
                Log.err(throwable);
            } else {
                Log.log(this.getArcLogLevel(level), string);
                if (throwable != null) {
                    Log.err(throwable);
                }
            }
        }
    }

    private boolean isArcLogLevelAtLeast(final Log.LogLevel level) {
        return level != LogLevel.none && Log.level.ordinal() <= level.ordinal();
    }

    private Log.LogLevel getArcLogLevel(final Level level) {
        return switch (level) {
            case TRACE, DEBUG -> Log.LogLevel.debug;
            case INFO -> Log.LogLevel.info;
            case WARN -> Log.LogLevel.warn;
            case ERROR -> Log.LogLevel.err;
        };
    }

    private String getColorCode(final Level level) {
        return switch (level) {
            case DEBUG, TRACE -> ColorCodes.lightCyan + ColorCodes.bold;
            case INFO -> ColorCodes.lightBlue + ColorCodes.bold;
            case WARN -> ColorCodes.lightYellow + ColorCodes.bold;
            case ERROR -> ColorCodes.lightRed + ColorCodes.bold;
        };
    }
}
