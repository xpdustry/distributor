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
import org.checkerframework.checker.nullness.qual.*;


/**
 * This class acts as a bridge between the {@link ArcCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as native arc commands.
 */
public final class ArcRegistrationHandler implements CommandRegistrationHandler{
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
        final var desc = command.getCommandMeta().getOrDefault(ArcMeta.DESCRIPTION, "");
        final var params = command.getCommandMeta().getOrDefault(ArcMeta.PARAMETERS, "[args...]");

        if(manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)){
            info.getAliases().forEach(handler::removeCommand);
        }

        final ObjectMap<String, CommandHandler.Command> commands = Reflect.get(handler, "commands");

        var added = false;
        for(final var alias : info.getAliases()){
            if(!commands.containsKey(alias)){
                final var cmd = new ArcNativeCommand(alias, params, desc, !alias.equals(info.getName()));
                commands.put(alias, cmd);
                handler.getCommandList().add(cmd);
                added = true;
            }
        }

        return added;
    }

    public final class ArcNativeCommand extends CommandHandler.Command{
        private final boolean alias;

        public ArcNativeCommand(@NonNull String name, @NonNull String params, @NonNull String description, boolean alias){
            super(name, params, description, (CommandRunner<Playerc>)(args, player) ->
                manager.handleCommand(player, args.length == 0 ? name : name + " " + args[0]));
            this.alias = alias;
        }

        public boolean isAlias(){
            return alias;
        }
    }
}
