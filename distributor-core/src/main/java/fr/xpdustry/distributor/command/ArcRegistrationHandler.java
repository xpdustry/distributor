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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;


/**
 * This class acts as a bridge between the {@code ArcCommandManager} and the {@code CommandHandler},
 * by registering cloud commands as native commands.
 */
public class ArcRegistrationHandler implements CommandRegistrationHandler{
    private final @NonNull CommandHandler handler;
    private final @NonNull ArcCommandManager manager;
    private final ObjectMap<String, CommandHandler.Command> commands;

    public ArcRegistrationHandler(@NonNull CommandHandler handler, @NonNull ArcCommandManager manager){
        this.handler = handler;
        this.manager = manager;
        this.commands = Reflect.get(handler, "commands");
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

        var modified = false;
        for(final var name : names){
            if(!commands.containsKey(name)){
                handler.register(name, "[args...]", desc, new ArcCommandRunner(name));
                modified = true;
            }
        }

        return modified;
    }

    public final class ArcCommandRunner implements CommandRunner<Playerc>{
        private final @NonNull String name;

        public ArcCommandRunner(@NonNull String name){
            this.name = name;
        }

        public @NonNull String getName(){
            return name;
        }

        @Override public void accept(@NonNull String[] args, @Nullable Playerc playerc){
            manager.handleCommand(playerc, args.length == 0 ? name : name + " " + args[0]);
        }
    }
}
