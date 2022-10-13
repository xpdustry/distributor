package fr.xpdustry.distributor.command.sender;

import arc.util.*;
import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

final class ConsoleCommandSender implements CommandSender {

  static final ConsoleCommandSender INSTANCE = new ConsoleCommandSender();

  private ConsoleCommandSender() {
  }

  @Override
  public void sendMessage(final @NotNull String content) {
    Log.info(content);
  }

  @Override
  public void sendWarning(final @NotNull String content) {
    Log.warn(content);
  }

  @Override
  public @NotNull Locale getLocale() {
    return Locale.getDefault();
  }

  @Override
  public @NotNull Optional<Player> getPlayer() {
    return Optional.empty();
  }
}
