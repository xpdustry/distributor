package fr.xpdustry.distributor.plugin;

import arc.util.*;
import java.io.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;
import org.slf4j.event.*;
import org.slf4j.helpers.*;

final class PluginLogger extends AbstractLogger {

  @Serial
  private static final long serialVersionUID = 3476499937056865545L;

  PluginLogger(final @NotNull String name) {
    this.name = name;
  }

  @Override
  protected @Nullable String getFullyQualifiedCallerName() {
    return null;
  }

  @Override
  protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
    final var builder = new StringBuilder();
    if (marker == null || !marker.contains("NO_PLUGIN_NAME")) {
      builder
        .append(ColorCodes.cyan)
        .append('[')
        .append(ColorCodes.white)
        .append(name)
        .append(ColorCodes.cyan)
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
}
