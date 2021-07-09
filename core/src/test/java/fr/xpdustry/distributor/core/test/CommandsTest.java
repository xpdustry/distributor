package fr.xpdustry.distributor.core.test;

import arc.math.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;

import fr.xpdustry.distributor.core.command.CommandResponse.*;
import fr.xpdustry.distributor.core.command.*;

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

        command.handleCommand(new String[]{arg1, arg2});
        assertTrue(executed[0], "sub command wasn't executed.");
    }

    @Test
    @DisplayName("Test CommandContainer")
    public void commandExecutorTest(){
        final boolean[] executed = {false, false};

        CommandContainer container = new CommandContainer("test", "<cmd> [params...]", "test");

        container.register("add", "<arg1> <arg2>", "add stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) + Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) + Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[0] = true;
        });

        container.register("mul", "<arg1> <arg2>", "multiply stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) * Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) * Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[1] = true;
        });

        container.handleCommand(new String[]{"add", arg1 + " " + arg2});
        assertTrue(executed[0], "add command wasn't executed.");

        container.handleCommand(new String[]{"mul", arg1 + " " + arg2});
        assertTrue(executed[1], "mul command wasn't executed.");
    }

    @Test
    @DisplayName("Test CommandHandler")
    public void commandHandlerTest(){
        final int[] executed = {0};

        CommandHandler handler = new CommandHandler("/");


        Command command = new Command("div", "<arg1> <arg2>", "divide stuff", (args, player) -> {
            int result = Integer.parseInt(args[0]) / Integer.parseInt(args[1]);
            int expected = Integer.parseInt(arg1) / Integer.parseInt(arg2);
            assertEquals(result, expected);
            executed[0]++;
        });

        CommandContainer container = new CommandContainer("test", "<cmd> [args...]", "test");
        container.register(command);


        Commands.registerToHandler(handler, command, container);

        handler.handleMessage(Strings.format("/@ @ @", command.name, arg1, arg2));
        assertEquals(1, executed[0], "standalone div command wasn't executed.");

        handler.handleMessage(Strings.format("/@ @ @ @", container.name, command.name, arg1, arg2));
        assertEquals(2, executed[0], "div command wasn't executed in the container.");
    }

    @Test
    @DisplayName("Test CommandResponse")
    public void commandResponseTest(){
        // Generates an ObjectMap with all the ResponseTypes and false as their default value
        final ObjectMap<ResponseType, Boolean> executed =
            Seq.with(ResponseType.values()).asMap(type -> type, type -> false);

        CommandContainer executor = new CommandContainer("test", "<arg1> [params...]", "test"){{
            runner = (args, player) -> {
                CommandResponse response = handleSubcommand(args[0], Arrays.copyOfRange(args, 1, args.length), null);
                executed.put(response.type, true);
            };
        }};

        // emptyExecutor case
        executor.handleCommand(new String[]{"add", arg1 + " " + arg2});

        executor.register("pow", "<arg1=(numeric)> <arg2=(numeric)>", "power stuff", (args, player) -> {
            if((Integer.parseInt(args[0]) | Integer.parseInt(args[1])) > 100){
                throw new RuntimeException("One of the arguments is too high !");
            }

            int result = Mathf.pow(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            int expected = Mathf.pow(Integer.parseInt(arg1), Integer.parseInt(arg2));

            assertEquals(result, expected);
        });

        // commandNotFound case
        executor.handleCommand(new String[]{"sub", arg1 + " " + arg2});

        // success case
        executor.handleCommand(new String[]{"pow", arg1 + " " + arg2});

        // tooManyArguments case
        executor.handleCommand(new String[]{"pow", arg1 + " " + arg2 + " " + "15"});

        // notEnoughArguments case
        executor.handleCommand(new String[]{"pow", arg1});

        // unhandledException case
        executor.handleCommand(new String[]{"pow", arg1 + " " + "200"});

        // badArguments case
        executor.handleCommand(new String[]{"pow", arg1 + " " + "ten"});

        // Test if all the cases have been tested and executed
        for(Entry<ResponseType, Boolean> entry : executed){
            assertTrue(entry.value, entry.key.name() + " case hasn't been executed");
        }
    }
}
