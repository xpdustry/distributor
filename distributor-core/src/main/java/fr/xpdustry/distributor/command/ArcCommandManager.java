package fr.xpdustry.distributor.command;

import arc.util.*;
import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.ArcRegistrationHandler.*;
import fr.xpdustry.distributor.command.argument.PlayerArgument.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.exception.*;
import fr.xpdustry.distributor.command.processor.*;
import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.Command;
import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import io.leangen.geantyref.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    private final CommandPermissionInjector permissionInjector = new CommandPermissionInjector();

    private final AnnotationParser<ArcCommandSender> annotationParser =
        new AnnotationParser<>(this, ArcCommandSender.class, p -> createDefaultCommandMeta());

    private Function<Player, ArcCommandSender> commandSenderMapper =
        p -> p == null ? new ArcServerSender() : new ArcClientSender(p);

    public ArcCommandManager(final @NotNull CommandHandler handler){
        super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
        setSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS, true);
        setCommandRegistrationHandler(new ArcRegistrationHandler(handler, this));
        setCaptionRegistry(new ArcCaptionRegistry());

        registerCommandPreProcessor(permissionInjector);
        getParserRegistry().registerParserSupplier(TypeToken.get(Player.class), p -> new PlayerParser<>());
    }

    /**
     * Execute a command and handle the result.
     *
     * @param sender the command sender
     * @param input the command input
     */
    @SuppressWarnings("FutureReturnValueIgnored")
    protected void handleCommand(final @NotNull ArcCommandSender sender, final @NotNull String input){
        executeCommand(sender, input).whenComplete((result, throwable) -> {
            if(throwable == null){
                return;
            }else if(throwable instanceof ArgumentParseException t){
                throwable = t.getCause();
            }

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
                Log.err(throwable);
            }else{
                StandardExceptionHandlers.COMMAND_FAILURE.accept(sender, throwable);
                Log.err(throwable);
            }
        });
    }

    /**
     * Execute a command and handle the result.
     *
     * @param player the player
     * @param input the command input
     */
    public void handleCommand(final @Nullable Player player, final @NotNull String input){
        handleCommand(commandSenderMapper.apply(player), input);
    }

    /**
     * Execute a command and handle the result.
     *
     * @param input the command input
     */
    public void handleCommand(final @NotNull String input){
        handleCommand(commandSenderMapper.apply(null), input);
    }

    /** @return the shared annotation parser instance of this command manager */
    public @NotNull AnnotationParser<ArcCommandSender> getAnnotationParser(){
        return annotationParser;
    }

    /** @return the shared command injector instance of this command manager */
    public @NotNull CommandPermissionInjector getPermissionInjector(){
        return permissionInjector;
    }

    /** @return the command sender mapping function */
    public @NotNull Function<Player, ArcCommandSender> getCommandSenderMapper(){
        return commandSenderMapper;
    }

    /**
     * Set the command sender mapping function.
     *
     * @param commandSenderMapper the new command mapping function
     */
    public void setCommandSenderMapper(
        final @NotNull Function<Player, @NotNull ArcCommandSender> commandSenderMapper
    ){
        this.commandSenderMapper = commandSenderMapper;
    }

    /**
     * Utility method that can convert a {@link CommandHandler.Command} to a {@link Command}.
     *
     * @param command the command to convert
     * @return the converted command
     *
     * @throws IllegalArgumentException if the command is a {@link CloudCommand}
     */
    public @NotNull Command<ArcCommandSender> convertNativeCommand(final CommandHandler.@NotNull Command command){
        if(command instanceof CloudCommand) throw new IllegalArgumentException(
                "You can't convert a cloud command that has been converted to a native command back to a cloud command..."
        );

        final var meta = SimpleCommandMeta.builder()
            .with(createDefaultCommandMeta())
            .with(ArcMeta.NATIVE, true)
            .with(ArcMeta.DESCRIPTION, command.description)
            .build();

        var builder = commandBuilder(command.text, meta)
            .handler(new NativeCommandExecutionHandler(command));

        for(final var parameter : command.params){
            final var argument = StringArgument.<ArcCommandSender>newBuilder(parameter.name);
            if(parameter.variadic) argument.greedy();
            if(parameter.optional) argument.asOptional();
            builder = builder.argument(argument);
        }

        return builder.build();
    }

    @Override public boolean hasPermission(final @NotNull ArcCommandSender sender, final @NotNull String permission){
        return sender.hasPermission(permission);
    }

    @Override public Command.@NotNull Builder<ArcCommandSender> commandBuilder(
        final @NotNull String name,
        final @NotNull CommandMeta meta,
        final @NotNull String... aliases
    ){
        return super.commandBuilder(name, meta, aliases).senderType(ArcCommandSender.class);
    }

    @Override public Command.@NotNull Builder<ArcCommandSender> commandBuilder(
        final @NotNull String name,
        final @NotNull String... aliases
    ){
        return super.commandBuilder(name, aliases).senderType(ArcCommandSender.class);
    }

    @Override public @NotNull CommandMeta createDefaultCommandMeta(){
        return SimpleCommandMeta.builder()
            .with(ArcMeta.NATIVE, false)
            .with(ArcMeta.PLUGIN, "unknown")
            .build();
    }

    /** A command execution handler that calls an underlying {@link CommandHandler.Command}. */
    public static final class NativeCommandExecutionHandler implements CommandExecutionHandler<ArcCommandSender>{
        private final CommandHandler.Command command;

        public NativeCommandExecutionHandler(final CommandHandler.@NotNull Command command){
            this.command = command;
        }

        @Override public void execute(final @NotNull CommandContext<ArcCommandSender> ctx){
            final CommandRunner<Player> runner = Reflect.get(this.command, "runner");
            final var array = ctx.getRawInput().toArray(new String[0]);
            // Removes the first argument because it's the name of the command
            final var args = Arrays.copyOfRange(array, 1, ctx.getRawInput().size());
            runner.accept(args, ctx.getSender().isPlayer() ? ctx.getSender().asPlayer() : null);
        }
    }
}
