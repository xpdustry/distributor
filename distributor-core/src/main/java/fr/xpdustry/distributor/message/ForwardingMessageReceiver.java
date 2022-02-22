package fr.xpdustry.distributor.message;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ForwardingMessageReceiver implements MessageReceiver {

  private final Collection<MessageReceiver> receivers;

  public ForwardingMessageReceiver(Collection<MessageReceiver> receivers) {
    this.receivers = receivers;
  }

  @Override
  public void sendMessage(@NotNull String message) {
    receivers.forEach(r -> r.sendMessage(message));
  }
}
