package fr.xpdustry.distributor.message;

import org.jetbrains.annotations.*;

final class ForwardingMessageReceiver implements MessageReceiver {

  private final Iterable<MessageReceiver> receivers;

  public ForwardingMessageReceiver(Iterable<MessageReceiver> receivers) {
    this.receivers = receivers;
  }

  @Override
  public void sendMessage(@NotNull String message) {
    receivers.forEach(r -> r.sendMessage(message));
  }
}
