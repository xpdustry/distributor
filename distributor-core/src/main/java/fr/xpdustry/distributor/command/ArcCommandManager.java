package fr.xpdustry.distributor.command;

import arc.util.*;
import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.Command;
import cloud.commandframework.*;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.function.*;

import static fr.xpdustry.distributor.command.caption.ArcCaptionKeys.*;
import static java.util.Objects.requireNonNull;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    public static final Key<Boolean> NATIVE_KEY = Key.of(Boolean.class, "distributor:native", m -> false);
    public static final Key<String> PLUGIN_KEY = Key.of(String.class, "distributor:plugin", m -> "unknown");

    private @NonNull String prefix = "/";
    private @NonNull BiFunction<Playerc, CaptionRegistry<ArcCommandSender>, ArcCommandSender> commandSenderMapper =
        (p, c) -> p == null ? new ArcConsoleSender(c) : new ArcPlayerSender(p, c);

    public ArcCommandManager(){
        super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
        setCaptionRegistry(new ArcCaptionRegistry());
    }

    public @NonNull Command<ArcCommandSender> convertNativeCommand(CommandHandler.@NonNull Command command){
        var builder = commandBuilder(command.text)
            .meta(NATIVE_KEY, true)
            .meta(PLUGIN_KEY, "mindustry")
            .meta(CommandMeta.DESCRIPTION, command.description)
            .handler(new ArcNativeCommandRunner(Reflect.get(command, "runner")));

        for(var parameter : command.params){
            final var argument = StringArgument.<ArcCommandSender>newBuilder(parameter.name);
            if(parameter.variadic) argument.greedy();
            if(parameter.optional) argument.asOptional();
            builder = builder.argument(argument);
        }

        return builder.build();
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    public boolean handleMessage(@NonNull ArcCommandSender sender, @NonNull String input){
        if(!input.startsWith(prefix)) return false;
        executeCommand(sender, input.substring(prefix.length())).whenComplete((result, throwable) -> {
            if(throwable == null) return;

            if(throwable instanceof InvalidSyntaxException t){
                handleException(sender, InvalidSyntaxException.class, t, (s, e) -> {
                    s.send(COMMAND_INVALID_SYNTAX, CaptionVariable.of("syntax", e.getCorrectSyntax()));
                });
            }else if(throwable instanceof NoPermissionException t){
                handleException(sender, NoPermissionException.class, t, (s, e) -> {
                    s.send(COMMAND_INVALID_PERMISSION, CaptionVariable.of("permission", e.getMissingPermission()));
                });
            }else if(throwable instanceof NoSuchCommandException t){
                handleException(sender, NoSuchCommandException.class, t, (s, e) -> {
                    s.send(COMMAND_FAILURE_NO_SUCH_COMMAND, CaptionVariable.of("command", e.getSuppliedCommand()));
                });
            }else if(throwable instanceof ArgumentParseException t){
                handleException(sender, ArgumentParseException.class, t, (s, e) -> {
                    if(e.getCause() instanceof ParserException p){
                        s.send(p.errorCaption(), p.captionVariables());
                    }else{
                        s.send(ARGUMENT_PARSE_FAILURE, CaptionVariable.of("message", e.getCause().getMessage()));
                    }
                });
            }else if(throwable instanceof CommandExecutionException t){
                handleException(sender, CommandExecutionException.class, t, (s, e) -> {
                    s.send(COMMAND_FAILURE_EXECUTION, CaptionVariable.of("cause", e.getCause().getMessage()));
                });
            }else{
                sender.send(COMMAND_FAILURE, CaptionVariable.of("message", throwable.getMessage()));
            }
        });

        return true;

    }

    public boolean handleMessage(@Nullable Playerc player, @NonNull String input){
        return handleMessage(commandSenderMapper.apply(player, getCaptionRegistry()), input);
    }

    public boolean handleMessage(@NonNull String input){
        return handleMessage((Playerc)null, input);
    }

    public @NonNull String getPrefix(){
        return prefix;
    }

    public void setPrefix(@NonNull String prefix){
        this.prefix = requireNonNull(prefix, "prefix can't be null.");
    }

    public @NonNull BiFunction<Playerc, CaptionRegistry<ArcCommandSender>, ArcCommandSender> getCommandSenderMapper(){
        return commandSenderMapper;
    }

    public void setCommandSenderMapper(@NonNull BiFunction<Playerc, CaptionRegistry<ArcCommandSender>, ArcCommandSender> commandSenderMapper){
        this.commandSenderMapper = requireNonNull(commandSenderMapper, "commandSenderMapper can't be null.");
    }

    @Override public boolean hasPermission(@NonNull ArcCommandSender sender, @NonNull String permission){
        return sender.hasPermission(permission);
    }

    @Override public Command.@NonNull Builder<ArcCommandSender> commandBuilder(@NonNull String name, @NonNull String... aliases){
        return super.commandBuilder(name, aliases)/*.senderType(ArcCommandSender.class)*/;
    }

    @Override public @NonNull CommandMeta createDefaultCommandMeta(){
        return CommandMeta.simple().build();
    }

    public static final class ArcNativeCommandRunner implements CommandExecutionHandler<ArcCommandSender>{
        private final @NonNull CommandRunner<Playerc> runner;

        public ArcNativeCommandRunner(@NonNull CommandRunner<Playerc> runner){
            this.runner = requireNonNull(runner, "runner can't be null.");
        }

        @Override public void execute(@NonNull CommandContext<ArcCommandSender> ctx){
            final var array = ctx.getRawInput().toArray(new String[0]);
            // Removes the first argument because it's the name of the command
            final var args = Arrays.copyOfRange(array, 1, ctx.getRawInput().size());
            final var player = ctx.getSender().isPlayer() ? ctx.getSender().asPlayer() : null;
            runner.accept(args, player);
        }
    }
}
