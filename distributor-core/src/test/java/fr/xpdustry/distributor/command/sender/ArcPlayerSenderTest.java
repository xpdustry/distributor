package fr.xpdustry.distributor.command.sender;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.util.*;

import cloud.commandframework.captions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ArcPlayerSenderTest{
    private Playerc player;
    private ArcPlayerSender sender;

    @BeforeEach
    public void setup(){
        player = new MockPlayer();
        sender = new ArcPlayerSender(player, new ArcCaptionRegistry());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NONE", "DEBUG", "INFO", "ERROR"})
    public void test_send_message(String intent){
        final var message = "Hello @";
        final var expected = formatString(intent, message, "Bob");

        sender.send(MessageIntent.valueOf(intent), message, "Bob");
        assertEquals(expected, sender.asPlayer().lastText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NONE", "DEBUG", "INFO", "ERROR"})
    public void test_send_caption(String intent){
        final var caption = StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER;
        final var expected = formatString(intent, "'@' is not a valid number in the range @ to @", "30", "10", "20");
        final var variables = new CaptionVariable[]{
            CaptionVariable.of("input", "30"),
            CaptionVariable.of("min", "10"),
            CaptionVariable.of("max", "20")
        };

        sender.send(MessageIntent.valueOf(intent), caption, variables);
        assertEquals(expected, sender.asPlayer().lastText());
    }

    public String formatString(String intent, String text, Object... args){
        return switch(intent){
            case "DEBUG" -> "[gray]" + Strings.format(text.replace("@", "[lightgray]@[]"), args);
            case "ERROR" -> "[scarlet]" + Strings.format(text.replace("@", "[orange]@[]"), args);
            default -> Strings.format(text, args);
        };
    }
}
