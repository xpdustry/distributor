package fr.xpdustry.distributor.message;

import org.jetbrains.annotations.*;

public interface MessageReceiver {

  void sendMessage(final @NotNull String content);
}
