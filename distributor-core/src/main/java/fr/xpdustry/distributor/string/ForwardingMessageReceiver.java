package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.CaptionVariable;
import fr.xpdustry.distributor.localization.Translator;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ForwardingMessageReceiver implements MessageReceiver {

  private final Collection<MessageReceiver> receivers;

  public ForwardingMessageReceiver(Collection<MessageReceiver> receivers) {
    this.receivers = receivers;
  }

  @Override
  public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args) {
    receivers.forEach(r -> r.sendMessage(intent, message, args));
  }

  @Override
  public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars) {
    receivers.forEach(r -> r.sendMessage(intent, message, vars));
  }
}
