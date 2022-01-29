package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.argument.PlayerArgument.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.exception.*;
import fr.xpdustry.distributor.command.processor.*;
import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import io.leangen.geantyref.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    private final CommandPermissionInjector permissionInjector = new CommandPermissionInjector();

    private final AnnotationParser<ArcCommandSender> annotationParser =
        new AnnotationParser<>(this, ArcCommandSender.class, p -> createDefaultCommandMeta());

    private BiFunction<CaptionRegistry<ArcCommandSender>, Player, ArcCommandSender> commandSenderMapper =
        (c, p) -> p == null ? new ArcServerSender(c) : new ArcClientSender(p, c);

    public ArcCommandManager(final @NonNull CommandHandler handler){
        super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
        setSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS, true);
        setCommandRegistrationHandler(new ArcRegistrationHandler(handler, this));
        setCaptionRegistry(new ArcCaptionRegistry());

        registerCommandPreProcessor(permissionInjector);
        getParserRegistry().registerParserSupplier(TypeToken.get(Player.class), p -> new PlayerParser<>());
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    public void handleCommand(final @NonNull ArcCommandSender sender, final @NonNull String input){
        executeCommand(sender, input).whenComplete((result, throwable) -> {
            if(throwable == null) return;
            if(throwable instanceof ArgumentParseException t) throwable = t.getCause();

            if(throwable instanceof InvalidSyntaxException t){
                handleException(sender, InvalidSyntaxException.class, t, StandardExceptionHandlers.COMMAND_INVALID_SYNTAX);
            }else if(throwable instanceof NoPermissionException t){
                handleException(sender, NoPermissionException.class, t, StandardExceptionHandlers.COMMAND_INVALID_PERMISSION);
            }else if(throwable instanceof NoSuchCommandException t){
                handleException(sender, NoSuchCommandException.class, t, StandardExceptionHandlers.COMMAND_FAILURE_NO_SUCH_COMMAND);
            }else if(throwable instanceof ParserException t){
                handleException(sender, ParserException.class, t, StandardExceptionHandlers.ARGUMENT_PARSE_FAILURE);
            }else if(throwable instanceof CommandExecutionException t){
                handleException(sender, CommandExecutionException.class, t, StandardExceptionHandlers.COMMAND_FAILURE_EXECUTION);
                Log.err(t);
            }else{
                StandardExceptionHandlers.COMMAND_FAILURE.accept(sender, throwable);
                Log.err(throwable);
            }
        });
    }

    public void handleCommand(final @Nullable Player player, final @NonNull String input){
        handleCommand(commandSenderMapper.apply(getCaptionRegistry(), player), input);
    }

    public void handleCommand(final @NonNull String input){
        handleCommand(commandSenderMapper.apply(getCaptionRegistry(), null), input);
    }

    public AnnotationParser<ArcCommandSender> getAnnotationParser(){
        return annotationParser;
    }

    public CommandPermissionInjector getPermissionInjector(){
        return permissionInjector;
    }

    public @NonNull BiFunction<@NonNull CaptionRegistry<ArcCommandSender>, Player, @NonNull ArcCommandSender> getCommandSenderMapper(){
        return commandSenderMapper;
    }

    public void setCommandSenderMapper(
        final @NonNull BiFunction<@NonNull CaptionRegistry<ArcCommandSender>, Player, @NonNull ArcCommandSender> commandSenderMapper
    ){
        this.commandSenderMapper = commandSenderMapper;
    }

    @Override public boolean hasPermission(final @NonNull ArcCommandSender sender, final @NonNull String permission){
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
