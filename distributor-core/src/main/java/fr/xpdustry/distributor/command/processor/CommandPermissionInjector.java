package fr.xpdustry.distributor.command.processor;

import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.execution.preprocessor.*;
import cloud.commandframework.permission.*;
import org.jetbrains.annotations.*;


import java.util.*;
import java.util.function.*;


public class CommandPermissionInjector implements CommandPreprocessor<ArcCommandSender>{
    private final Map<String, Predicate<ArcCommandSender>> injectors = new HashMap<>();

    public void registerInjector(
        final @NotNull String permission,
        final @NotNull Predicate<@NotNull ArcCommandSender> predicate
    ){
        injectors.put(permission, predicate);
    }

    public void registerInjector(
        final @NotNull CommandPermission permission,
        final @NotNull Predicate<@NotNull ArcCommandSender> predicate
    ){
        registerInjector(permission.toString(), predicate);
    }

    @Override
    public void accept(final @NotNull CommandPreprocessingContext<ArcCommandSender> ctx){
        injectors.forEach((p, i) -> {
            if(i.test(ctx.getCommandContext().getSender())) ctx.getCommandContext().getSender().addPermission(p);
        });
    }
}
