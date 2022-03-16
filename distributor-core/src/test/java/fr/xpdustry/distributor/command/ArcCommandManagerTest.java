package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.permission.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.message.format.*;
import fr.xpdustry.distributor.struct.*;
import fr.xpdustry.distributor.util.*;
import mindustry.gen.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ArcCommandManagerTest {

  private CommandHandler handler;
  private ArcCommandManager manager;

  @BeforeEach
  void setup() {
    handler = new CommandHandler("/");
    manager = new ArcCommandManager(handler, ArcClientSender::new, MessageFormatter::simple);
  }

  @Test
  void test_command_exception_syntax() {
    final var exceptionHandler = new TestCommandExceptionHandler<InvalidSyntaxException>();
    manager.registerExceptionHandler(InvalidSyntaxException.class, exceptionHandler);

    manager.command(manager.commandBuilder("test"));
    manager.handleCommand("test 1 5 9");

    assertNotNull(exceptionHandler.getLastException());
  }

  @Test
  void test_command_exception_permission() {
    final var exceptionHandler = new TestCommandExceptionHandler<NoPermissionException>();
    manager.registerExceptionHandler(NoPermissionException.class, exceptionHandler);

    manager.command(manager.commandBuilder("test").permission(Permission.of("wow")));
    manager.handleCommand(Player.create(), "test");

    assertNotNull(exceptionHandler.getLastException());
  }

  @Test
  void test_command_exception_no_such_command() {
    final var exceptionHandler = new TestCommandExceptionHandler<NoSuchCommandException>();
    manager.registerExceptionHandler(NoSuchCommandException.class, exceptionHandler);

    // The following line stays until https://github.com/Incendo/cloud/issues/337 is solved
    manager.command(manager.commandBuilder("test1"));
    manager.handleCommand("test2");

    assertNotNull(exceptionHandler.getLastException());
  }

  @Test
  void test_command_exception_parsing() {
    final var exceptionHandler = new TestCommandExceptionHandler<ParserException>();
    manager.registerExceptionHandler(ParserException.class, exceptionHandler);

    manager.command(manager.commandBuilder("test").argument(IntegerArgument.of("num")));
    manager.handleCommand("test 10.5");

    assertNotNull(exceptionHandler.getLastException());
  }

  @Test
  void test_command_exception_execution() {
    final var exceptionHandler = new TestCommandExceptionHandler<CommandExecutionException>();
    manager.registerExceptionHandler(CommandExecutionException.class, exceptionHandler);

    manager.command(manager.commandBuilder("test").handler(ctx -> {throw new RuntimeException();}));
    manager.handleCommand("test");

    assertNotNull(exceptionHandler.getLastException());
  }

  @Test
  void test_native_command_execution_handler() {
    final var executed = Holder.getBool();
    final var nativeCommand = handler.<Player>register("test", "None", (args, player) -> executed.set(true));
    final var cloudCommand = manager.convertNativeCommand(nativeCommand);
    manager.command(cloudCommand);
    manager.handleCommand("test");

    assertEquals(Boolean.TRUE, executed.get());
  }
}
