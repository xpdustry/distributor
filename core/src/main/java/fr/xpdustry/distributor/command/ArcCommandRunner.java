package fr.xpdustry.distributor.command;

import arc.util.CommandHandler.*;

import mindustry.gen.*;

import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.execution.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;

import static java.util.Objects.requireNonNull;


public class ArcCommandRunner implements CommandRunner<Playerc>{
    protected final @NonNull String commandName;
    protected final @NonNull ArcCommandManager commandManager;
    protected final @NonNull Function<Playerc, ArcCommandSender> senderMapper;

    public ArcCommandRunner(@NonNull String commandName, @NonNull ArcCommandManager commandManager,
                            @NonNull Function<Playerc, ArcCommandSender> senderMapper){
        this.commandName = requireNonNull(commandName, "command can't be null.");
        this.commandManager = requireNonNull(commandManager, "manager can't be null.");
        this.senderMapper = requireNonNull(senderMapper, "commandSenderMapper can't be null.");
    }

    public @NonNull String getCommandName(){
        return commandName;
    }

    public @NonNull ArcCommandManager getCommandManager(){
        return commandManager;
    }

    public @NonNull Function<Playerc, ArcCommandSender> getSenderMapper(){
        return senderMapper;
    }

    @Override
    public void accept(String[] args, Playerc playerc){
        commandManager.executeCommand(senderMapper.apply(playerc), commandName + " " + String.join(" ", args))
            .whenComplete(this::handleCommandResult);
    }

    public void handleCommandResult(@NonNull CommandResult<ArcCommandSender> result, @Nullable Throwable throwable){
        if(throwable == null) return;

        final var sender = result.getCommandContext().getSender();

        /*
        if(throwable instanceof InvalidSyntaxException t){
            commandManager.handleException(sender, InvalidSyntaxException.class, t,
                commandManager.getCaptionRegistry().getCaption()
                (s, e) -> s.send(ArcCommandManager.MESSAGE_INVALID_SYNTAX + e.getCorrectSyntax())
            );
        }else if(throwable instanceof InvalidCommandSenderException t){
            commandManager.handleException(sender, InvalidCommandSenderException.class, t,
                (s, e) -> s.send(e.getMessage())
            );
        }else if(throwable instanceof NoPermissionException t){
            commandManager.handleException(sender, NoPermissionException.class, t,
                (s, e) -> s.send(ArcCommandManager.MESSAGE_NO_PERMS)
            );
        }else if(throwable instanceof NoSuchCommandException t){
            commandManager.handleException(sender, NoSuchCommandException.class, t,
                (s, e) -> s.send(ArcCommandManager.MESSAGE_UNKNOWN_COMMAND)
            );
        }else if(throwable instanceof ArgumentParseException t){
            if(t.getCause() instanceof ParserException p){
                sender.send(p.errorCaption().getKey());
            }
            commandManager.handleException(sender, ArgumentParseException.class, t,
                (c, e) -> c.send("Invalid Command Argument: " + e.getCause().getMessage())
            );
        }else if(throwable instanceof CommandExecutionException t){
            commandManager.handleException(sender, CommandExecutionException.class, t,
                (c, e) -> {
                    c.send(ArcCommandManager.MESSAGE_INTERNAL_ERROR);
                    e.printStackTrace();
                }
            );
        }else{
            sender.send(throwable.getMessage());
        }

         */
    }
}
