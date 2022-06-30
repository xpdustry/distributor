package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.CloudCapability.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.permission.*;
import fr.xpdustry.distributor.admin.*;
import java.util.function.*;
import mindustry.gen.*;
import mindustry.mod.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;

public class ArcCommandManager<C> extends CommandManager<C> {

  private final Plugin plugin;

  private final Function<OnlinePlayer, C> commandSenderMapper;
  private final Function<C, OnlinePlayer> backwardsCommandSenderMapper;

  public ArcCommandManager(
    final @NotNull Plugin plugin,
    final @NotNull CommandHandler handler,
    final @NotNull Function<OnlinePlayer, C> commandSenderMapper,
    final @NotNull Function<C, OnlinePlayer> backwardsCommandSenderMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    registerCapability(StandardCapabilities.ROOT_COMMAND_DELETION);
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));

    this.plugin = plugin;
    this.commandSenderMapper = commandSenderMapper;
    this.backwardsCommandSenderMapper = backwardsCommandSenderMapper;
  }

  public @NotNull Function<OnlinePlayer, C> getCommandSenderMapper() {
    return commandSenderMapper;
  }

  public @NotNull Function<C, OnlinePlayer> getBackwardsCommandSenderMapper() {
    return backwardsCommandSenderMapper;
  }

  public @NotNull Plugin getPlugin() {
    return plugin;
  }

  @Override
  public boolean hasPermission(final @NonNull C sender, final @NonNull String permission) {
    // TODO Add case for console senders
    return permission.isBlank() || backwardsCommandSenderMapper.apply(sender).hasPermission(permission);
  }

  @Override
  public @NonNull CommandMeta createDefaultCommandMeta() {
    // TODO Implement defaults
    return CommandMeta.simple().build();
  }
}
