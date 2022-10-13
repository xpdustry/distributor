package fr.xpdustry.distributor.command.sender;

import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

public interface CommandSender {

  static @NotNull CommandSender player(final Player player) {
    return new PlayerCommandSender(player);
  }

  static @NotNull CommandSender console() {
    return ConsoleCommandSender.INSTANCE;
  }

  void sendMessage(final @NotNull String content);

  void sendWarning(final @NotNull String content);

  @NotNull Locale getLocale();

  @NotNull Optional<Player> getPlayer();
}
