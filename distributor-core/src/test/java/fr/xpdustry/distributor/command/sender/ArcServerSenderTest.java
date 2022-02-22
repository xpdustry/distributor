package fr.xpdustry.distributor.command.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import arc.util.Log;
import arc.util.Log.LogLevel;
import arc.util.Strings;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.SimpleCaptionRegistry;
import fr.xpdustry.distributor.message.MessageIntent;
import fr.xpdustry.distributor.util.TestLogHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ArcServerSenderTest {
/*
  private ArcServerSender sender;
  private TestLogHandler logger;

  @BeforeAll
  public static void before() {
    Log.formatter = (text, useColors, args) -> Strings.format(text.replace("@", "&fb&lb@&fr"), args);
    Log.level = LogLevel.debug;
  }

  @BeforeEach
  public void setup() {
    sender = new ArcServerSender();
    logger = new TestLogHandler();
    Log.logger = logger;
  }

  @ParameterizedTest
  @ValueSource(strings = {"NONE", "DEBUG", "INFO", "ERROR"})
  public void test_send_message(String intent) {
    final var message = "Hello @";
    final var expected = "Hello &fb&lbBob&fr";

    sender.sendMessage(MessageIntent.valueOf(intent), message, "Bob");
    assertEquals(expected, logger.getLastText());
    checkIntent(intent);
  }

  @ParameterizedTest
  @ValueSource(strings = {"NONE", "DEBUG", "INFO", "ERROR"})
  public void test_send_caption(String intent) {
    final var expected = "'&fb&lb30&fr' is not a valid number in the range &fb&lb10&fr to &fb&lb20&fr";
    final var variables = new CaptionVariable[] {
      CaptionVariable.of("input", "30"),
      CaptionVariable.of("min", "10"),
      CaptionVariable.of("max", "20")
    };

    sender.sendMessage(MessageIntent.valueOf(intent), SimpleCaptionRegistry.ARGUMENT_PARSE_FAILURE_NUMBER, variables);
    assertEquals(expected, logger.getLastText());
    checkIntent(intent);
  }

  public void checkIntent(String intent) {
    assertTrue(switch (intent) {
      case "DEBUG" -> logger.getLastLevel() == LogLevel.debug;
      case "ERROR" -> logger.getLastLevel() == LogLevel.err;
      case "NONE", "INFO" -> logger.getLastLevel() == LogLevel.info;
      default -> throw new IllegalArgumentException("Unable to resolve log level: " + intent);
    });
  }

 */
}
