package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.ArcRegistrationHandler.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.util.*;

import cloud.commandframework.arguments.standard.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;


public class ArcRegistrationHandlerTest{
    private CommandHandler handler;
    private ArcCommandManager manager;
    private TestCommandExecutionHandler<ArcCommandSender> executor;

    @BeforeEach
    public void setup(){
        handler = new CommandHandler("/");
        manager = new ArcCommandManager(handler);
        executor = new TestCommandExecutionHandler<>();
    }

    @Test
    public void test_register_native_command(){
        manager.command(manager.commandBuilder("bob", "b"));
        manager.command(manager.commandBuilder("marine", "m"));
        manager.command(manager.commandBuilder("ashley", "a"));

        assertEquals(handler.getCommandList().size, 6);
        for(final var command : handler.getCommandList().<ArcNativeCommand>as()){
            assertNotNull(manager.getCommandTree().getNamedNode(command.text));

            if(command.text.length() == 1){
                assertTrue(command.isAlias(), command.text + " should be an alias.");
            }else{
                assertFalse(command.isAlias(), command.text + " shouldn't be an alias.");
            }
        }
    }

    @Test
    public void test_native_command_override(){
        final var command = handler.register("execute", "Execute something...", (args, parameter) -> {});

        assertTrue(handler.getCommandList().contains(command));
        manager.command(manager.commandBuilder("execute"));
        assertFalse(handler.getCommandList().contains(command));
    }

    @Test
    public void test_native_command_execution(){
        manager.command(manager.commandBuilder("test").handler(executor));
        manager.handleCommand("test");
        assertNotNull(executor.getLastContext());
    }

    @ParameterizedTest
    @ValueSource(strings = {"[args...]", "<a> <b> <c>"})
    void test_native_command_arguments(String parameters){
        final var input = "test 12 78 09";

        manager.command(manager.commandBuilder("test")
            .meta(ArcMeta.PARAMETERS, parameters)
            .argument(StringArgument.greedy("args"))
            .handler(executor));
        handler.handleMessage("/" + input);

        assertEquals(parameters, handler.getCommandList().get(0).paramText);
        assertNotNull(executor.getLastContext());
        assertEquals(input, executor.getLastContext().getRawInputJoined());
    }
}
