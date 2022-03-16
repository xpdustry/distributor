package fr.xpdustry.distributor.message;

import mindustry.gen.*;
import org.jetbrains.annotations.*;

final class ClientMessageReceiver implements MessageReceiver {

  private final Player player;

  public ClientMessageReceiver(final @NotNull Player player) {
    this.player = player;
  }

  @Override
  public void sendMessage(final @NotNull String message) {
    player.sendMessage(message);
  }
}
