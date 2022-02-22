package fr.xpdustry.distributor.util;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestCommandExecutionHandler<C> implements CommandExecutionHandler<C> {

  private @Nullable CommandContext<C> lastContext = null;

  @Override
  public void execute(@NotNull CommandContext<C> ctx) {
    lastContext = ctx;
  }

  public @Nullable CommandContext<C> getLastContext() {
    return lastContext;
  }
}
