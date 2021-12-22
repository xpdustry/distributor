package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import arc.util.CommandHandler.CommandRunner;

import mindustry.gen.*;

import cloud.commandframework.*;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.internal.*;
import org.checkerframework.checker.nullness.qual.*;

import static java.util.Objects.requireNonNull;


class ArcRegistrationHandler implements CommandRegistrationHandler{
    public final String CLOUD_ARGUMENTS = "[args...]";

    private final @NonNull CommandHandler handler;
    private final @NonNull ArcCommandManager manager;
    private final ObjectMap<String, CommandHandler.Command> commands;

    public ArcRegistrationHandler(@NonNull CommandHandler handler, @NonNull ArcCommandManager manager){
        this.handler = requireNonNull(handler, "handler can't be null.");
        this.manager = requireNonNull(manager, "manager can't be null.");
        this.commands = Reflect.get(handler, "commands");
    }

    public @NonNull CommandHandler getHandler(){
        return handler;
    }

    public @NonNull ArcCommandManager getManager(){
        return manager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean registerCommand(@NonNull Command<?> command){
        final var info = (StaticArgument<ArcCommandSender>)command.getArguments().get(0);
        final var name = info.getName();
        final var aliases = info.getAlternativeAliases();

        if(manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)){
            handler.removeCommand(name);
            aliases.forEach(handler::removeCommand);
        }

        if(!commands.containsKey(name)){
            registerCommandPath(name);
            aliases.stream().filter(a -> !commands.containsKey(a)).forEach(this::registerCommandPath);
            return true;
        }else{
            return false;
        }
    }

    public void registerCommandPath(@NonNull String name){
        handler.register(name, CLOUD_ARGUMENTS, "", new ArcCommandRunner(name, manager));
    }

    public static final class ArcCommandRunner implements CommandRunner<Playerc>{
        private final @NonNull String commandName;
        private final @NonNull ArcCommandManager manager;

        public ArcCommandRunner(@NonNull String commandName, @NonNull ArcCommandManager manager){
            this.commandName = requireNonNull(commandName, "command can't be null.");
            this.manager = requireNonNull(manager, "manager can't be null.");
        }

        public @NonNull String getCommandName(){
            return commandName;
        }

        public @NonNull ArcCommandManager getManager(){
            return manager;
        }

        @Override
        public void accept(String[] args, Playerc playerc){
            manager.executeCommand(manager.getCommandSenderMapper().apply(playerc), commandName + " " + String.join(" ", args))
                .whenComplete(manager.getCommandResultHandler());
        }
    }
}
