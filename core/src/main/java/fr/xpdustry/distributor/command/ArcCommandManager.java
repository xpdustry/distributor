package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import cloud.commandframework.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import org.checkerframework.checker.nullness.qual.*;

import java.lang.reflect.*;
import java.util.function.*;

import static java.util.Objects.*;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    public static final Field COMMAND_RUNNER_ACCESSOR;

    static{
        try{
            COMMAND_RUNNER_ACCESSOR = CommandHandler.Command.class.getDeclaredField("runner");
            COMMAND_RUNNER_ACCESSOR.setAccessible(true);
        }catch(NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    private @NonNull Function<Playerc, ArcCommandSender> commandSenderMapper = ArcCommandSender::new;
    private @NonNull BiConsumer<CommandResult<ArcCommandSender>, Throwable> commandResultHandler;
    private @NonNull BiPredicate<ArcCommandSender, String> permissionHandler = (s, p) -> switch(p){
        case "admin" -> s.isAdmin();
        case "" -> true;
        default -> false;
    };

    public ArcCommandManager(@NonNull CommandHandler handler){
        super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
        setCommandRegistrationHandler(new ArcRegistrationHandler(handler, this));
        this.commandResultHandler = (r, t) -> {};
    }

    @Override public @NonNull ArcRegistrationHandler getCommandRegistrationHandler(){
        return (ArcRegistrationHandler)super.getCommandRegistrationHandler();
    }

    public @NonNull Function<Playerc, ArcCommandSender> getCommandSenderMapper(){
        return commandSenderMapper;
    }

    public void setCommandSenderMapper(@NonNull Function<Playerc, ArcCommandSender> commandSenderMapper){
        this.commandSenderMapper = requireNonNull(commandSenderMapper, "commandSenderMapper can't be null.");
    }

    public @NonNull BiConsumer<CommandResult<ArcCommandSender>, Throwable> getCommandResultHandler(){
        return commandResultHandler;
    }

    public void setCommandResultHandler(@NonNull BiConsumer<CommandResult<ArcCommandSender>, Throwable> commandResultHandler){
        this.commandResultHandler = requireNonNull(commandResultHandler, "commandResultHandler can't be null.");
    }

    public @NonNull BiPredicate<ArcCommandSender, String> getPermissionHandler(){
        return permissionHandler;
    }

    public void setPermissionHandler(@NonNull BiPredicate<ArcCommandSender, String> permissionHandler){
        this.permissionHandler = requireNonNull(permissionHandler, "permissionHandler can't be null.");
    }

    @Override public boolean hasPermission(@NonNull ArcCommandSender sender, @NonNull String permission){
        return permissionHandler.test(sender, permission);
    }

    @Override public @NonNull CommandMeta createDefaultCommandMeta(){
        return CommandMeta.simple().build();
    }
}
