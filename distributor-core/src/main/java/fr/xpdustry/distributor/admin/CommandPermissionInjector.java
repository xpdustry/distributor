package fr.xpdustry.distributor.admin;

import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.execution.preprocessor.*;
import cloud.commandframework.permission.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.function.*;


public class CommandPermissionInjector implements CommandPreprocessor<ArcCommandSender>{
    private final Map<String, Predicate<ArcCommandSender>> injectors = new HashMap<>();

    public void registerInjector(
        final @NonNull String permission,
        final @NonNull Predicate<@NonNull ArcCommandSender> predicate
    ){
        injectors.put(permission, predicate);
    }

    public void registerInjector(
        final @NonNull CommandPermission permission,
        final @NonNull Predicate<@NonNull ArcCommandSender> predicate
    ){
        registerInjector(permission.toString(), predicate);
    }

    @Override
    public void accept(@NonNull CommandPreprocessingContext<ArcCommandSender> ctx){
        injectors.forEach((p, i) -> {
            if(i.test(ctx.getCommandContext().getSender())) ctx.getCommandContext().getSender().addPermission(p);
        });
    }
}
