package fr.xpdustry.distributor.command.sender;

import arc.util.Log;
import arc.util.Nullable;
import cloud.commandframework.captions.CaptionVariable;
import fr.xpdustry.distributor.string.ColoringMessageFormatter;
import fr.xpdustry.distributor.string.MessageFormatter;
import fr.xpdustry.distributor.string.MessageIntent;
import java.util.Locale;
import java.util.function.Consumer;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;


public class ArcServerSender extends ArcCommandSender {

  public ArcServerSender(final @NotNull MessageFormatter formatter) {
    super(formatter);
  }

  public ArcServerSender() {
    super(ServerMessageFormatter.getInstance());
  }

  @Override
  public boolean isPlayer() {
    return false;
  }

  @Override
  public @NotNull Player asPlayer() {
    throw new UnsupportedOperationException("Cannot convert console to player");
  }

  /**
   * @return the {@link Locale#getDefault() default locale} of the system.
   */
  @Override
  public @NotNull Locale getLocale() {
    return Locale.getDefault();
  }

  /** Since it's the console, it always returns true. */
  @Override
  public boolean hasPermission(final @NotNull String permission) {
    return true;
  }

  @Override
  public void sendMessage(final @NotNull MessageIntent intent, final @NotNull String message, final @Nullable Object... args) {
    getLogger(intent).accept(getFormatter().format(intent, message, args));
  }

  @Override
  public void sendMessage(final @NotNull MessageIntent intent, final @NotNull String message, final @NotNull CaptionVariable... vars) {
    getLogger(intent).accept(getFormatter().format(intent, message, vars));
  }

  protected Consumer<String> getLogger(final @NotNull MessageIntent intent) {
    return switch (intent) {
      case DEBUG -> Log::debug;
      case ERROR -> Log::err;
      default -> Log::info;
    };
  }

  /**
   * This formatter performs the formatting of a default mindustry server where arguments are colored.
   */
  public static class ServerMessageFormatter implements ColoringMessageFormatter {

    private static final ServerMessageFormatter INSTANCE = new ServerMessageFormatter();

    public static ServerMessageFormatter getInstance() {
      return INSTANCE;
    }

    @Override
    public @NotNull String prefix(final @NotNull MessageIntent intent) {
      return "";
    }

    @Override
    public @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg) {
      return "&fb&lb" + arg + "&fr";
    }
  }
}
