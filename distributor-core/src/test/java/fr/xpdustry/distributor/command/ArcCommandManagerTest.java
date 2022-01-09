package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.sender.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class ArcCommandManagerTest{
    private CommandHandler handler;
    private ArcCommandManager manager;

    @BeforeEach
    public void setup(){
        handler = new CommandHandler("/");
        manager = new ArcCommandManager(handler);
    }

    @Test
    public void test_server_sender_mapping(){
        assertInstanceOf(ArcConsoleSender.class, manager.getCommandSenderMapper().apply(null, manager.getCaptionRegistry()));
    }

    @Test
    public void test_player_sender_mapping(){
        assertInstanceOf(ArcPlayerSender.class, manager.getCommandSenderMapper().apply(Player.create(), manager.getCaptionRegistry()));
    }

    @Test
    public void test_register_native_command(){
        manager.command(manager.commandBuilder("bob", "b"));
        manager.command(manager.commandBuilder("marine", "m"));
        manager.command(manager.commandBuilder("ashley", "a"));

        assertEquals(handler.getCommandList().size, 6);
        for(final var command : handler.getCommandList()){
            assertNotNull(manager.getCommandTree().getNamedNode(command.text));
        }
    }

    @Test
    public void test_native_command_override(){
        final var command = handler.register("execute", "Execute something...", (args, parameter) -> {});
        assertTrue(handler.getCommandList().contains(command));
        manager.command(manager.commandBuilder("execute"));
        assertFalse(handler.getCommandList().contains(command));
    }
}
