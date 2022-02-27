package fr.xpdustry.distributor.message;

import arc.util.Log;
import org.jetbrains.annotations.NotNull;

final class ServerMessageReceiver implements MessageReceiver {

  static final ServerMessageReceiver INSTANCE = new ServerMessageReceiver();

  @Override
  public void sendMessage(@NotNull String message) {
    Log.info(message);
  }
}
