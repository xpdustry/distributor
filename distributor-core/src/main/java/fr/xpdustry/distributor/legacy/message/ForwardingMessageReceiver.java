package fr.xpdustry.distributor.legacy.message;

import org.jetbrains.annotations.*;

final class ForwardingMessageReceiver implements MessageReceiver {

  private final Iterable<MessageReceiver> receivers;

  public ForwardingMessageReceiver(final @NotNull Iterable<MessageReceiver> receivers) {
    this.receivers = receivers;
  }

  @Override
  public void sendMessage(final @NotNull String message) {
    receivers.forEach(r -> r.sendMessage(message));
  }
}
