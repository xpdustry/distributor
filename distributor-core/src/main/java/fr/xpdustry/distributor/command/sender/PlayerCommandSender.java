package fr.xpdustry.distributor.command.sender;

import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

final class PlayerCommandSender implements CommandSender {

  private final Player player;
  private final Locale locale;

  PlayerCommandSender(final @NotNull Player player) {
    this.player = player;
    this.locale = Locale.forLanguageTag(player.locale().replace('_', '-'));
  }

  @Override
  public void sendMessage(final @NotNull String content) {
    player.sendMessage(content);
  }

  @Override
  public void sendWarning(final @NotNull String content) {
    player.sendMessage("[red]" + content);
  }

  @NotNull
  @Override
  public Locale getLocale() {
    return locale;
  }

  @Override
  public @NotNull Optional<Player> getPlayer() {
    return Optional.of(player);
  }
}
