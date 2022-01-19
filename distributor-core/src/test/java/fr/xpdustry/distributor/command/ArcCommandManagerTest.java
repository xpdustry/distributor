package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.util.*;

import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.permission.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ArcCommandManagerTest{
    private ArcCommandManager manager;

    @BeforeEach
    public void setup(){
        manager = new ArcCommandManager(new CommandHandler("/"));
    }

    @Test
    public void test_command_exception_syntax(){
        final var exceptionHandler = new TestCommandExceptionHandler<InvalidSyntaxException>();
        manager.registerExceptionHandler(InvalidSyntaxException.class, exceptionHandler);

        manager.command(manager.commandBuilder("test"));
        manager.handleCommand("test 1 5 9");

        assertNotNull(exceptionHandler.getLastException());
    }

    @Test
    public void test_command_exception_permission(){
        final var exceptionHandler = new TestCommandExceptionHandler<NoPermissionException>();
        manager.registerExceptionHandler(NoPermissionException.class, exceptionHandler);

        manager.command(manager.commandBuilder("test").permission(Permission.of("wow")));
        manager.handleCommand(Player.create(), "test");

        assertNotNull(exceptionHandler.getLastException());
    }

    @Test
    public void test_command_exception_no_such_command(){
        final var exceptionHandler = new TestCommandExceptionHandler<NoSuchCommandException>();
        manager.registerExceptionHandler(NoSuchCommandException.class, exceptionHandler);

        // The following stays until https://github.com/Incendo/cloud/issues/337 is solved
        manager.command(manager.commandBuilder("test1"));
        manager.handleCommand("test2");

        assertNotNull(exceptionHandler.getLastException());
    }

    @Test
    public void test_command_exception_parsing(){
        final var exceptionHandler = new TestCommandExceptionHandler<ParserException>();
        manager.registerExceptionHandler(ParserException.class, exceptionHandler);

        manager.command(manager.commandBuilder("test").argument(IntegerArgument.of("num")));
        manager.handleCommand("test 10.5");

        assertNotNull(exceptionHandler.getLastException());
    }

    @Test
    public void test_command_exception_execution(){
        final var exceptionHandler = new TestCommandExceptionHandler<CommandExecutionException>();
        manager.registerExceptionHandler(CommandExecutionException.class, exceptionHandler);

        manager.command(manager.commandBuilder("test").handler(ctx -> {throw new RuntimeException();}));
        manager.handleCommand("test");

        assertNotNull(exceptionHandler.getLastException());
    }
}