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
package fr.xpdustry.distributor.logging;

import arc.util.*;
import fr.xpdustry.distributor.plugin.*;
import java.io.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;
import org.slf4j.event.*;
import org.slf4j.helpers.*;

public class ArcLogger extends AbstractLogger {

  @Serial
  private static final long serialVersionUID = 3476499937056865545L;

  private final @Nullable Class<?> clazz;
  private final Type type;

  @SuppressWarnings("unchecked")
  ArcLogger(final @NotNull String name) {
    Class<?> clazz = null;
    try {
      clazz = Class.forName(name);
    } catch (final ClassNotFoundException ignored) {
    }

    this.clazz = clazz;

    if (clazz != null) {
      if (Plugin.class.isAssignableFrom(clazz)) {
        this.name = PluginDescriptor.from((Class<? extends Plugin>) clazz).getDisplayName();
        this.type = Type.PLUGIN;
      } else {
        this.name = clazz.getSimpleName();
        this.type = Type.CLASS;
      }
    } else {
      this.name = name;
      this.type = Type.NAMED;
    }
  }

  @Override
  protected @Nullable String getFullyQualifiedCallerName() {
    return clazz != null ? name : null;
  }

  @Override
  protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
    final var builder = new StringBuilder();
    if (marker == null || (type == Type.PLUGIN && !marker.contains("NO_PLUGIN_NAME"))) {
      final var color = switch (type) {
        case NAMED -> ColorCodes.white;
        case CLASS -> ColorCodes.green;
        case PLUGIN -> ColorCodes.cyan;
      };
      builder
        .append(color)
        .append('[')
        .append(ColorCodes.white)
        .append(name)
        .append(color)
        .append(']')
        .append(ColorCodes.reset)
        .append(' ');
    }
    builder.append(MessageFormatter.basicArrayFormat(messagePattern, arguments));
    if (arguments != null && arguments.length == 0 && throwable != null && getNativeLogLevel(level) == Log.LogLevel.err) {
      Log.err(builder.toString(), throwable);
    } else {
      Log.log(getNativeLogLevel(level), builder.toString());
      if (throwable != null) {
        Log.err(throwable);
      }
    }
  }

  @Override
  public boolean isTraceEnabled() {
    return isNativeLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return isNativeLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isDebugEnabled() {
    return isNativeLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return isNativeLogLevelAtLeast(Log.LogLevel.debug);
  }

  @Override
  public boolean isInfoEnabled() {
    return isNativeLogLevelAtLeast(Log.LogLevel.info);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return isNativeLogLevelAtLeast(Log.LogLevel.info);
  }

  @Override
  public boolean isWarnEnabled() {
    return isNativeLogLevelAtLeast(Log.LogLevel.warn);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return isNativeLogLevelAtLeast(Log.LogLevel.warn);
  }

  @Override
  public boolean isErrorEnabled() {
    return isNativeLogLevelAtLeast(Log.LogLevel.err);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return isNativeLogLevelAtLeast(Log.LogLevel.err);
  }

  private boolean isNativeLogLevelAtLeast(final Log.LogLevel level) {
    return Log.level.ordinal() <= level.ordinal();
  }

  private Log.LogLevel getNativeLogLevel(final Level level) {
    return switch (level) {
      case TRACE, DEBUG -> Log.LogLevel.debug;
      case INFO -> Log.LogLevel.info;
      case WARN -> Log.LogLevel.warn;
      case ERROR -> Log.LogLevel.err;
    };
  }

  private enum Type {
    NAMED, CLASS, PLUGIN
  }
}
