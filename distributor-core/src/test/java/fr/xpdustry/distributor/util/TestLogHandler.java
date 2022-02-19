package fr.xpdustry.distributor.util;

import arc.util.Log.LogHandler;
import arc.util.Log.LogLevel;
import org.jetbrains.annotations.Nullable;


public class TestLogHandler implements LogHandler {

  @Nullable
  private LogLevel lastLevel = null;
  @Nullable
  private String lastText = null;

  @Override
  public void log(LogLevel level, String text) {
    lastLevel = level;
    lastText = text;
  }


  public @Nullable LogLevel getLastLevel() {
    return lastLevel;
  }

  public @Nullable String getLastText() {
    return lastText;
  }
}
