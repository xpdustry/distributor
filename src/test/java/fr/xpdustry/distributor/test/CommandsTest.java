package fr.xpdustry.distributor.test;

import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;

import fr.xpdustry.distributor.command.*;
import static fr.xpdustry.distributor.command.Commands.*;

import fr.xpdustry.distributor.command.CommandResponse.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class CommandsTest{
    public static String arg1 = "10";
    public static String arg2 = "5";

    @Test
    @DisplayName("Test Command")
    public void commandTest(){
        final boolean[] executed = {false};

        Command command = new Command("sub", "<arg1> <arg2>", "sub stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) - Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) - Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[0] = true;
        });

        command.handleCommand(new String[]{arg1, arg2}, null);
        assertTrue(executed[0], "sub command wasn't executed.");
    }

    @Test
    @DisplayName("Test CommandExecutor")
    public void commandExecutorTest(){
        final boolean[] executed = {false, false};

        CommandContainer executor = new CommandContainer("test", "<cmd> [params...]", "test");

        executor.register("add", "<arg1> <arg2>", "add stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) + Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) + Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[0] = true;
        });

        executor.register("mul", "<arg1> <arg2>", "multiply stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) * Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) * Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[1] = true;
        });

        executor.handleCommand(new String[]{"add", arg1, arg2}, null);
        executor.handleCommand(new String[]{"mul", arg1, arg2}, null);

        assertTrue(executed[0], "add command wasn't executed.");
        assertTrue(executed[1], "mul command wasn't executed.");
    }

    @Test
    @DisplayName("Test CommandHandler")
    public void commandHandlerTest(){
        final boolean[] executed = {false};

        CommandHandler handler = new CommandHandler("/");

        Command command = new Command("div", "<arg1> <arg2>", "divide stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) / Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) / Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[0] = true;
        });

        registerToHandler(handler, command);

        handler.handleMessage(Strings.format("/@ @ @", command.name, arg1, arg2));

        assertTrue(executed[0], "div command wasn't executed.");
    }

    @Test
    @DisplayName("Test CommandResponse")
    public void commandResponseTest(){
        final ObjectMap<ResponseType, Boolean> executed = ObjectMap.of(
            ResponseType.success, false,
            ResponseType.emptyExecutor, false,
            ResponseType.commandNotFound, false,
            ResponseType.tooManyArguments, false,
            ResponseType.notEnoughArguments, false,
            ResponseType.unhandledException, false
        );

        CommandContainer executor = new CommandContainer("fr/xpdustry/distributor", "<arg1> [params...]", "fr/xpdustry/distributor"){{
            runner = (args, player) -> {
                CommandResponse response = handleSubcommand(args[0], Arrays.copyOfRange(args, 1, args.length), null);
                executed.put(response.type, true);
            };
        }};

        // emptyExecutor case
        executor.handleCommand(new String[]{"add", arg1, arg2}, null);

        executor.register("pow", "<arg1> <arg2>", "power stuff", (args, player) -> {
            int result = Mathf.pow(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            int expected = Mathf.pow(Integer.parseInt(arg1), Integer.parseInt(arg2));
            assertEquals(result, expected);
        });

        // commandNotFound case
        executor.handleCommand(new String[]{"sub", arg1, arg2}, null);

        // success case
        executor.handleCommand(new String[]{"pow", arg1, arg2}, null);

        // tooManyArguments case
        executor.handleCommand(new String[]{"pow", arg1, arg2, "15"}, null);

        // notEnoughArguments case
        executor.handleCommand(new String[]{"pow", arg1}, null);

        // unhandledException case (NumberFormatException)
        executor.handleCommand(new String[]{"pow", arg1, "10.5"}, null);

        for(Entry<ResponseType, Boolean> entry : executed){
            assertTrue(entry.value, entry.key.name() + " case hasn't been executed");
        }
    }
}
