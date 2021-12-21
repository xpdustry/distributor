package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import cloud.commandframework.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.units.qual.*;

import java.lang.reflect.*;
import java.util.function.*;

import static java.util.Objects.requireNonNull;


public class ArcCommandManager extends CommandManager<ArcCommandSender>{
    public static final String MESSAGE_INTERNAL_ERROR = "An internal error occurred while attempting to perform this command.";
    public static final String MESSAGE_INVALID_SYNTAX = "Invalid Command Syntax. Correct command syntax is: ";
    public static final String MESSAGE_NO_PERMS = "You do not have permission to perform this command.";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final Field COMMAND_RUNNER_ACCESSOR;

    static{
        try{
            COMMAND_RUNNER_ACCESSOR = CommandHandler.Command.class.getDeclaredField("runner");
            COMMAND_RUNNER_ACCESSOR.setAccessible(true);
        }catch(NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    private @NonNull Function<Playerc, ArcCommandSender> mapper;

    public ArcCommandManager(@NonNull CommandHandler handler){
        this(handler, ArcCommandSender::new);
        requireState(RegistrationState.BEFORE_REGISTRATION);
    }

    public ArcCommandManager(@NonNull CommandHandler handler, @NonNull Function<Playerc, ArcCommandSender> mapper){
        super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
        this.mapper = mapper;
        setCommandRegistrationHandler(new ArcRegistrationHandler(handler, this));
    }

    @Override
    public boolean hasPermission(@NonNull ArcCommandSender sender, @NonNull String permission){
        return switch(permission){
            case "admin" -> sender.isAdmin();
            default -> true;
        };
    }

    @Override
    public @NonNull CommandMeta createDefaultCommandMeta(){
        return CommandMeta.simple().build();
    }

}
