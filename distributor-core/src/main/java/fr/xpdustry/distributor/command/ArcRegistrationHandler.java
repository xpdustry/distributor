package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import org.checkerframework.checker.nullness.qual.*;


/**
 * This class acts as a bridge between the {@code ArcCommandManager} and the {@code CommandHandler},
 * by registering cloud commands as native commands.
 */
public class ArcRegistrationHandler implements CommandRegistrationHandler{
    private final @NonNull CommandHandler handler;
    private final @NonNull ArcCommandManager manager;

    public ArcRegistrationHandler(@NonNull CommandHandler handler, @NonNull ArcCommandManager manager){
        this.handler = handler;
        this.manager = manager;
    }

    public @NonNull CommandHandler getHandler(){
        return handler;
    }

    public @NonNull ArcCommandManager getManager(){
        return manager;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean registerCommand(@NonNull Command<?> command){
        final var info = (StaticArgument<ArcCommandSender>)command.getArguments().get(0);
        final var names = info.getAliases();
        final var desc = command.getCommandMeta().getOrDefault(CommandMeta.DESCRIPTION, "");

        if(manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)){
            names.forEach(handler::removeCommand);
        }

        final ObjectMap<String, CommandHandler.Command> commands = Reflect.get(handler, "commands");

        var modified = false;
        for(final var name : names){
            if(!commands.containsKey(name)){
                final var nativeCommand = new ArcNativeCommand(name, desc);
                commands.put(name, nativeCommand);
                handler.getCommandList().add(nativeCommand);
                modified = true;
            }
        }

        return modified;
    }

    public final class ArcNativeCommand extends CommandHandler.Command{
        public ArcNativeCommand(String name, String description){
            super(name, "[args...]", description, (CommandRunner<Playerc>)(args, player) -> {
                manager.handleCommand(player, args.length == 0 ? name : name + " " + args[0]);
            });
        }
    }
}
