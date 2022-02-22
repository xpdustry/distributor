package fr.xpdustry.distributor.message;

import arc.util.Log;
import org.jetbrains.annotations.NotNull;

public final class ServerMessageReceiver implements MessageReceiver {
  private static final ServerMessageReceiver INSTANCE = new ServerMessageReceiver();

  private ServerMessageReceiver() {
  }

  public static ServerMessageReceiver getInstance() {
    return INSTANCE;
  }

  @Override
  public void sendMessage(@NotNull String message) {
    Log.info(message);
  }
}
