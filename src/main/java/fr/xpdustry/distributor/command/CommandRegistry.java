package fr.xpdustry.distributor.command;

import arc.util.Nullable;
import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.LambdaCommand.*;
import fr.xpdustry.xcommand.*;

import org.jetbrains.annotations.*;

import java.util.*;


/**
 * Utility class to keep track of commands for a given plugin or script.
 */
public class CommandRegistry implements Iterable<Command<Playerc>>{
    private final SortedMap<String, Command<Playerc>> commands = new TreeMap<>();
    private @NotNull CommandRegistry.CommandAdapterFactory wrapperFactory = CommandAdapter::new;

    /**
     * Adds a command to the registry.
     *
     * @param command the command to add, not null
     * @return the command
     */
    public Command<Playerc> register(@NotNull Command<Playerc> command){
        commands.put(command.getName(), command);
        return command;
    }

    /**
     * Adds a command from a {@code LambdaCommandBuilder}.
     *
     * @param builder the builder of the command, not null
     * @return the built command
     */
    public @NotNull Command<Playerc> register(@NotNull LambdaCommandBuilder<Playerc> builder){
        return register(builder.build());
    }

    /**
     * Utility method that returns a new {@code LambdaCommandBuilder} with the player type.
     *
     * @param name the name of the command, not null
     * @return a new {@code LambdaCommandBuilder}
     */
    public @NotNull LambdaCommandBuilder<Playerc> builder(@NotNull String name){
        return LambdaCommand.of(name, Commands.PLAYER_TYPE);
    }

    public @Nullable Command<Playerc> getCommand(@NotNull String name){
        return commands.get(name);
    }

    public boolean contains(@NotNull String commandName){
        return commands.containsKey(commandName);
    }

    public boolean contains(@NotNull Command<Playerc> command){
        return commands.containsValue(command);
    }

    public @NotNull SortedMap<String, Command<Playerc>> getCommands(){
        return new TreeMap<>(commands);
    }

    public void setWrapperFactory(@NotNull CommandRegistry.CommandAdapterFactory wrapperFactory){
        this.wrapperFactory = wrapperFactory;
    }

    public void export(@NotNull CommandHandler handler){
        for(var command : commands.values()){
            handler.register(command.getName(), Commands.getParameterText(command),
                command.getDescription(), wrapperFactory.wrap(command));
        }
    }

    public void dispose(@NotNull CommandHandler handler){
        for(var command : commands.values()){
            handler.removeCommand(command.getName());
        }
    }

    /** @return an iterator over the commands of the registry. */
    @Override
    public @NotNull Iterator<Command<Playerc>> iterator(){
        return commands.values().iterator();
    }

    /** @return a spliterator over the commands of the registry. */
    @Override
    public @NotNull Spliterator<Command<Playerc>> spliterator(){
        return commands.values().spliterator();
    }

    /**
     * A simple interface to create new {@code CommandAdapter} instances.
     */
    @FunctionalInterface
    public interface CommandAdapterFactory{
        CommandAdapter wrap(@NotNull Command<Playerc> command);
    }
}
