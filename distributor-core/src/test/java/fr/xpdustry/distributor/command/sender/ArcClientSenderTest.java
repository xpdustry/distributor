package fr.xpdustry.distributor.command.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import arc.util.Strings;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.SimpleCaptionRegistry;
import fr.xpdustry.distributor.string.MessageIntent;
import fr.xpdustry.distributor.util.TestPlayer;
import mindustry.gen.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class ArcClientSenderTest {

  private Player player;
  private ArcClientSender sender;

  @BeforeEach
  public void setup() {
    player = new TestPlayer();
    sender = new ArcClientSender(player);
  }

  @ParameterizedTest
  @ValueSource(strings = {"NONE", "DEBUG", "INFO", "ERROR", "SUCCESS"})
  public void test_send_message(String intent) {
    final var message = "Hello @";
    final var expected = formatString(intent, message, "Bob");

    sender.sendMessage(MessageIntent.valueOf(intent), message, "Bob");
    assertEquals(expected, player.lastText());
  }

  @ParameterizedTest
  @ValueSource(strings = {"NONE", "DEBUG", "INFO", "ERROR", "SUCCESS"})
  public void test_send_caption(String intent) {
    final var expected = formatString(intent, "'@' is not a valid number in the range @ to @", "30", "10", "20");
    final var variables = new CaptionVariable[]{
      CaptionVariable.of("input", "30"),
      CaptionVariable.of("min", "10"),
      CaptionVariable.of("max", "20")
    };

    sender.sendMessage(MessageIntent.valueOf(intent), SimpleCaptionRegistry.ARGUMENT_PARSE_FAILURE_NUMBER, variables);
    assertEquals(expected, player.lastText());
  }

  public String formatString(String intent, String text, Object... args) {
    return switch (intent) {
      case "DEBUG" -> "[gray]" + Strings.format(text.replace("@", "[lightgray]@[]"), args);
      case "ERROR" -> "[scarlet]" + Strings.format(text.replace("@", "[orange]@[]"), args);
      case "SUCCESS" -> Strings.format(text.replace("@", "[green]@[]"), args);
      case "NONE", "INFO" -> Strings.format(text, args);
      default -> throw new IllegalArgumentException("Unable to resolve formatter: " + intent);
    };
  }
}
