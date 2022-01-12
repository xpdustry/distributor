package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    private @NonNull BiFunction<@NonNull CaptionRegistry<ArcCommandSender>, @Nullable Playerc, @NonNull ArcCommandSender> commandSenderMapper =
        (c, p) -> p == null ? new ArcServerSender(c) : new ArcClientSender(p, c);

    public ArcCommandManager(@NonNull CommandHandler handler){
        super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
        setSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS, true);
        setCommandRegistrationHandler(new ArcRegistrationHandler(handler, this));
        setCaptionRegistry(new ArcCaptionRegistry());
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    public void handleCommand(@NonNull ArcCommandSender sender, @NonNull String input){
        executeCommand(sender, input).whenComplete((result, throwable) -> {
            if(throwable == null) return;

            // TODO better code formatting ???

            if(throwable instanceof InvalidSyntaxException t){
                handleException(sender, InvalidSyntaxException.class, t, (s, e) -> {
                    s.send(
                        MessageIntent.ERROR, ArcCaptionKeys.COMMAND_INVALID_SYNTAX,
                        CaptionVariable.of("syntax", e.getCorrectSyntax()));
                });
            }else if(throwable instanceof NoPermissionException t){
                handleException(sender, NoPermissionException.class, t, (s, e) -> {
                    s.send(
                        MessageIntent.ERROR, ArcCaptionKeys.COMMAND_INVALID_PERMISSION,
                        CaptionVariable.of("permission", e.getMissingPermission()));
                });
            }else if(throwable instanceof NoSuchCommandException t){
                handleException(sender, NoSuchCommandException.class, t, (s, e) -> {
                    s.send(
                        MessageIntent.ERROR, ArcCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND,
                        CaptionVariable.of("command", e.getSuppliedCommand()));
                });
            }else if(throwable instanceof ArgumentParseException t){
                handleException(sender, ArgumentParseException.class, t, (s, e) -> {
                    if(e.getCause() instanceof ParserException p){
                        s.send(
                            MessageIntent.ERROR, p.errorCaption(),
                            p.captionVariables());
                    }else{
                        s.send(
                            MessageIntent.ERROR, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE,
                            CaptionVariable.of("message", e.getCause().getMessage()));
                    }
                });
            }else if(throwable instanceof CommandExecutionException t){
                handleException(sender, CommandExecutionException.class, t, (s, e) -> {
                    s.send(
                        MessageIntent.ERROR, ArcCaptionKeys.COMMAND_FAILURE_EXECUTION,
                        CaptionVariable.of("cause", e.getCause().getMessage()));
                });
            }else{
                sender.send(
                    MessageIntent.ERROR, ArcCaptionKeys.COMMAND_FAILURE,
                    CaptionVariable.of("message", throwable.getMessage()));
            }
        });
    }

    public void handleCommand(@Nullable Playerc player, @NonNull String input){
        handleCommand(commandSenderMapper.apply(getCaptionRegistry(), player), input);
    }

    public @NonNull BiFunction<@NonNull CaptionRegistry<ArcCommandSender>, @Nullable Playerc, @NonNull ArcCommandSender> getCommandSenderMapper(){
        return commandSenderMapper;
    }

    public void setCommandSenderMapper(@NonNull BiFunction<@NonNull CaptionRegistry<ArcCommandSender>, @Nullable Playerc, @NonNull ArcCommandSender> commandSenderMapper){
        this.commandSenderMapper = commandSenderMapper;
    }

    @Override public boolean hasPermission(@NonNull ArcCommandSender sender, @NonNull String permission){
        return sender.hasPermission(permission);
    }

    @Override public Command.@NonNull Builder<ArcCommandSender> commandBuilder(
        final @NonNull String name,
        final @NonNull CommandMeta meta,
        final @NonNull String... aliases
    ){
        return super.commandBuilder(name, meta, aliases).senderType(ArcCommandSender.class);
    }

    @Override public Command.@NonNull Builder<ArcCommandSender> commandBuilder(
        final @NonNull String name,
        final @NonNull String... aliases
    ){
        return super.commandBuilder(name, aliases).senderType(ArcCommandSender.class);
    }

    @Override public @NonNull CommandMeta createDefaultCommandMeta(){
        return SimpleCommandMeta.empty();
    }
}
