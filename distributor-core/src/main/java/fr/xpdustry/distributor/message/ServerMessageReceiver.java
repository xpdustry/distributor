package fr.xpdustry.distributor.message;

import arc.util.*;
import org.jetbrains.annotations.*;

final class ServerMessageReceiver implements MessageReceiver {

  static final ServerMessageReceiver INSTANCE = new ServerMessageReceiver();

  @Override
  public void sendMessage(@NotNull String message) {
    Log.info(message);
  }
}
