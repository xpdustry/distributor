/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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

import arc.util.*;
import fr.xpdustry.distributor.api.plugin.*;
import java.io.*;
import java.util.*;
import mindustry.mod.*;
import org.slf4j.*;
import org.slf4j.event.*;
import org.slf4j.helpers.*;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ArcLogger extends AbstractLogger {

  @Serial
  private static final long serialVersionUID = 3476499937056865545L;

  @SuppressWarnings("unchecked")
  public ArcLogger(final String name) {
    try {
      final var caller = Class.forName(name);
      if (Plugin.class.isAssignableFrom(caller)) {
        this.name = PluginDescriptor.from((Class<? extends Plugin>) caller).getDisplayName();
      } else {
        this.name = caller.getSimpleName();
      }
    } catch (final ClassNotFoundException ignored) {
      this.name = name;
    }
  }

  @Override
  public boolean isTraceEnabled() {
    return isArcLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return isArcLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isDebugEnabled() {
    return isArcLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return isArcLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isInfoEnabled() {
    return isArcLogLevelAtLeast(Log.LogLevel.info);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return isArcLogLevelAtLeast(Log.LogLevel.info);
  }

  @Override
  public boolean isWarnEnabled() {
    return isArcLogLevelAtLeast(Log.LogLevel.warn);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return isArcLogLevelAtLeast(Log.LogLevel.warn);
  }

  @Override
  public boolean isErrorEnabled() {
    return isArcLogLevelAtLeast(Log.LogLevel.err);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return isArcLogLevelAtLeast(Log.LogLevel.err);
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
    @Nullable Throwable throwable
  ) {
    final var builder = new StringBuilder();

    if (!name.equals(ROOT_LOGGER_NAME)) {
      builder
        .append(ColorCodes.white)
        .append('[')
        .append(ColorCodes.reset)
        .append(name)
        .append(ColorCodes.white)
        .append(']')
        .append(ColorCodes.reset)
        .append(' ');
    }

    if (throwable == null && arguments != null && arguments.length != 0 && arguments[arguments.length - 1] instanceof Throwable last) {
      throwable = last;
      arguments = arguments.length == 1 ? null : Arrays.copyOf(arguments, arguments.length - 1);
    }

    final var string = builder
      .append(MessageFormatter.basicArrayFormat(messagePattern.replace("{}", "&fb&lb{}&fr"), arguments))
      .toString();

    if (throwable != null && (arguments == null || arguments.length == 0)) {
      Log.err(string, throwable);
    } else {
      Log.log(getArcLogLevel(level), string);
      if (throwable != null) {
        Log.err(throwable);
      }
    }
  }

  private boolean isArcLogLevelAtLeast(final Log.LogLevel level) {
    return Log.level.ordinal() <= level.ordinal();
  }

  private Log.LogLevel getArcLogLevel(final Level level) {
    return switch (level) {
      case TRACE, DEBUG -> Log.LogLevel.debug;
      case INFO -> Log.LogLevel.info;
      case WARN -> Log.LogLevel.warn;
      case ERROR -> Log.LogLevel.err;
    };
  }
}
