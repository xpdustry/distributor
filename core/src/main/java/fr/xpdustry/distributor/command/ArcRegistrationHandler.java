package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import cloud.commandframework.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.internal.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.requireNonNull;


class ArcRegistrationHandler implements CommandRegistrationHandler{
    private final @NonNull CommandHandler handler;
    private final @NonNull ArcCommandManager manager;
    private final Map<String, Set<Command<ArcCommandSender>>> commands = new HashMap<>();

    public ArcRegistrationHandler(@NonNull CommandHandler handler, @NonNull ArcCommandManager manager){
        this.handler = requireNonNull(handler, "handler can't be null.");
        this.manager = requireNonNull(manager, "manager can't be null.");
    }

    public @NonNull CommandHandler getHandler(){
        return handler;
    }

    public @NonNull ArcCommandManager getManager(){
        return manager;
    }

    @Override
    public boolean registerCommand(@NonNull Command<?> command){
        // CommandComponent.of(StaticArgument.of(commandName, aliases), description
        final var info = command.getComponents().get(0);
        final var name = info.getArgument().getName();
        final var desc = info.getArgumentDescription().getDescription();

        handler.

        /*
        handler.<Playerc>register(name, Commands.getParameterText(command), desc, (args, player) -> {
            manager.executeCommand(new ArcCommandSender(player), name + " " + String.join(" ", args))
                .whenComplete((result, t) -> {
                    if(t == null) return;

                    t.printStackTrace();

                    final var sender = result.getCommandContext().getSender();

                    if(t instanceof InvalidSyntaxException){
                        manager.handleException(sender, InvalidSyntaxException.class, (InvalidSyntaxException)t,
                            (s, e) -> s.send(ArcCommandManager.MESSAGE_INVALID_SYNTAX + e.getCorrectSyntax())
                        );
                    }else if(t instanceof InvalidCommandSenderException){
                        manager.handleException(sender, InvalidCommandSenderException.class, (InvalidCommandSenderException)t,
                            (s, e) -> s.send(e.getMessage())
                        );
                    }else if(t instanceof NoPermissionException){
                        manager.handleException(sender, NoPermissionException.class, (NoPermissionException)t,
                            (s, e) -> s.send(ArcCommandManager.MESSAGE_NO_PERMS)
                        );
                    }else if(t instanceof NoSuchCommandException){
                        manager.handleException(sender, NoSuchCommandException.class, (NoSuchCommandException)t,
                            (s, e) -> s.send(ArcCommandManager.MESSAGE_UNKNOWN_COMMAND)
                        );
                    }else if(t instanceof ArgumentParseException){
                        manager.handleException(sender, ArgumentParseException.class, (ArgumentParseException)t,
                            (c, e) -> c.send("Invalid Command Argument: " + e.getCause().getMessage())
                        );
                    }else if(t instanceof CommandExecutionException){
                        manager.handleException(sender, CommandExecutionException.class, (CommandExecutionException)t,
                            (c, e) -> {
                                c.send(ArcCommandManager.MESSAGE_INTERNAL_ERROR);
                                e.printStackTrace();
                            }
                        );
                    }else{
                        sender.send(t.getMessage());
                    }
                });
        });

         */

        return true;
    }
}
