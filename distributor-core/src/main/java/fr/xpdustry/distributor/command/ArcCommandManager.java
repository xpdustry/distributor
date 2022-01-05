package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.keys.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.*;
import cloud.commandframework.permission.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    public static final Key<String> PLUGIN_KEY = Key.of(String.class, "distributor:plugin", m -> "unknown");
    public static final PredicatePermission<ArcCommandSender> ADMIN_PERMISSION =
        PredicatePermission.of(SimpleCloudKey.of("admin"), s -> !s.isPlayer() || (s.isPlayer() && s.asPlayer().admin()));

    private @NonNull BiFunction<Playerc, CaptionRegistry<ArcCommandSender>, ArcCommandSender> commandSenderMapper =
        (p, c) -> p == null ? new ArcConsoleSender(c) : new ArcPlayerSender(p, c);

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
        handleCommand(commandSenderMapper.apply(player, getCaptionRegistry()), input);
    }

    public @NonNull BiFunction<Playerc, CaptionRegistry<ArcCommandSender>, ArcCommandSender> getCommandSenderMapper(){
        return commandSenderMapper;
    }

    public void setCommandSenderMapper(@NonNull BiFunction<Playerc, CaptionRegistry<ArcCommandSender>, ArcCommandSender> commandSenderMapper){
        this.commandSenderMapper = commandSenderMapper;
    }

    @Override public boolean hasPermission(@NonNull ArcCommandSender sender, @NonNull String permission){
        return sender.hasPermission(permission);
    }

    @Override public Command.@NonNull Builder<ArcCommandSender> commandBuilder(@NonNull String name, @NonNull CommandMeta meta, @NonNull String... aliases){
        return super.commandBuilder(name, meta, aliases).senderType(ArcCommandSender.class);
    }

    @Override public Command.@NonNull Builder<ArcCommandSender> commandBuilder(@NonNull String name, @NonNull String... aliases){
        return super.commandBuilder(name, aliases).senderType(ArcCommandSender.class);
    }

    @Override public @NonNull CommandMeta createDefaultCommandMeta(){
        return CommandMeta.simple().build();
    }
}
