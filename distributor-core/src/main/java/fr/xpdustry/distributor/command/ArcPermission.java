package fr.xpdustry.distributor.command;

import fr.xpdustry.distributor.command.sender.*;

import cloud.commandframework.keys.*;
import cloud.commandframework.permission.*;


public final class ArcPermission{
    public static final PredicatePermission<ArcCommandSender> ADMIN =
        PredicatePermission.of(SimpleCloudKey.of("admin"), s -> !s.isPlayer() || (s.isPlayer() && s.asPlayer().admin()));
}
