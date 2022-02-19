package fr.xpdustry.distributor.string;

import arc.util.Nullable;
import arc.util.Strings;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.CaptionVariableReplacementHandler;
import cloud.commandframework.captions.SimpleCaptionVariableReplacementHandler;
import org.jetbrains.annotations.NotNull;


/**
 * This formatter performs basic formatting without any variations specified by {@link MessageIntent intents}.
 */
public final class SimpleMessageFormatter implements MessageFormatter {

  private static final SimpleMessageFormatter INSTANCE = new SimpleMessageFormatter();
  private static final CaptionVariableReplacementHandler HANDLER = new SimpleCaptionVariableReplacementHandler();

  public static SimpleMessageFormatter getInstance() {
    return INSTANCE;
  }

  @Override
  public @NotNull String format(
    final @NotNull MessageIntent intent,
    final @NotNull String message,
    final @Nullable Object... args
  ) {
    return Strings.format(message, args);
  }

  @Override
  public @NotNull String format(
    final @NotNull MessageIntent intent,
    final @NotNull String message,
    final @NotNull CaptionVariable... vars
  ) {
    return HANDLER.replaceVariables(message, vars);
  }
}
