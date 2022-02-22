package fr.xpdustry.distributor.message;

import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;

public final class ClientMessageReceiver implements MessageReceiver {
  private final Player player;

  public ClientMessageReceiver(final @NotNull Player player) {
    this.player = player;
  }

  @Override
  public void sendMessage(final @NotNull String message) {
    player.sendMessage(message);
  }
}
