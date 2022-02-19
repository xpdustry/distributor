package fr.xpdustry.distributor.command.sender;

import cloud.commandframework.captions.CaptionVariable;
import fr.xpdustry.distributor.string.ColoringMessageFormatter;
import fr.xpdustry.distributor.string.MessageFormatter;
import fr.xpdustry.distributor.string.MessageIntent;
import java.util.Locale;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ArcClientSender extends ArcCommandSender {

  private final @NotNull Player player;

  public ArcClientSender(final @NotNull Player player, final @NotNull MessageFormatter formatter) {
    super(formatter);
    this.player = player;
  }

  public ArcClientSender(final @NotNull Player player) {
    this(player, ClientMessageFormatter.getInstance());
  }

  @Override
  public boolean isPlayer() {
    return true;
  }

  @Override
  public @NotNull Player asPlayer() {
    return player;
  }

  @Override
  public @NotNull Locale getLocale() {
    return Locale.forLanguageTag(player.locale().replace('_', '-'));
  }

  @Override
  public void sendMessage(final @NotNull MessageIntent intent, final @NotNull String message, final @Nullable Object... args) {
    player.sendMessage(getFormatter().format(intent, message, args));
  }

  @Override
  public void sendMessage(final @NotNull MessageIntent intent, final @NotNull String message, final @NotNull CaptionVariable... vars) {
    player.sendMessage(getFormatter().format(intent, message, vars));
  }

  /**
   * This formatter performs special formatting for players. Here is an example with the message {@code There are '@'
   * players}:
   * <ul>
   *     <li>{@link MessageIntent#NONE NONE}: {@code There are '@' players.}</li>
   *     <li>{@link MessageIntent#DEBUG DEBUG}: {@code [gray]There are [lightgray]'@'[] players.}</li>
   *     <li>{@link MessageIntent#INFO INFO}: {@code There are '@' players.}</li>
   *     <li>{@link MessageIntent#ERROR ERROR}: {@code [scarlet]There are [orange]'@'[] players.}</li>
   * </ul>
   */
  public static class ClientMessageFormatter implements ColoringMessageFormatter {

    private static final ClientMessageFormatter INSTANCE = new ClientMessageFormatter();

    public static ClientMessageFormatter getInstance() {
      return INSTANCE;
    }

    @Override
    public @NotNull String prefix(final @NotNull MessageIntent intent) {
      return switch (intent) {
        case DEBUG -> "[gray]";
        case ERROR -> "[scarlet]";
        default -> "";
      };
    }

    @Override
    public @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg) {
      return switch (intent) {
        case DEBUG -> "[lightgray]" + arg + "[]";
        case ERROR -> "[orange]" + arg + "[]";
        case SUCCESS -> "[green]" + arg + "[]";
        default -> arg;
      };
    }
  }
}
