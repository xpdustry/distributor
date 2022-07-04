package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.*;
import java.util.function.*;
import mindustry.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;

public class MindustryCommandManager<C> extends CommandManager<C> {

  /**
   * The owning plugin of the command.
   */
  public static final Key<String> PLUGIN = Key.of(String.class, "distributor:plugin");

  private final Plugin plugin;

  private final SenderToCallerMapper<C> senderToCallerMapper;
  private final CallerToSenderMapper<C> callerToSenderMapper;

  public MindustryCommandManager(
    final @NotNull Plugin plugin,
    final @NotNull CommandHandler handler,
    final @NotNull SenderToCallerMapper<C> senderToCallerMapper,
    final @NotNull CallerToSenderMapper<C> callerToSenderMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    commandRegistrationHandler(new MindustryRegistrationHandler<>(this, handler));
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);

    this.plugin = plugin;
    this.callerToSenderMapper = callerToSenderMapper;
    this.senderToCallerMapper = senderToCallerMapper;
  }

  public @NotNull CallerToSenderMapper<C> getCallerToSenderMapper() {
    return callerToSenderMapper;
  }

  public @NotNull SenderToCallerMapper<C> getSenderToCallerMapper() {
    return senderToCallerMapper;
  }

  public final @NotNull Plugin getPlugin() {
    return plugin;
  }

  @Override
  public boolean hasPermission(final @NotNull C caller, final @NotNull String permission) {
    if (permission.isBlank()) {
      return true;
    } else {
      final var sender = callerToSenderMapper.apply(caller);
      return sender.isAdministrator() || sender.hasPermission(permission);
    }
  }

  @Override
  public @NotNull CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple()
      .with(PLUGIN, getPluginInternalName())
      .build();
  }

  private @NotNull String getPluginInternalName() {
    return Vars.mods.list().find(m -> m.main != null && m.main.getClass().equals(plugin.getClass())).name;
  }

  @FunctionalInterface
  public interface CallerToSenderMapper<C> extends Function<C, CommandSender> {

    static @NotNull CallerToSenderMapper<CommandSender> self() {
      return caller -> caller;
    }

    @Override
    @NotNull CommandSender apply(final @NotNull C caller);
  }

  @FunctionalInterface
  public interface SenderToCallerMapper<C> extends Function<CommandSender, C> {

    static @NotNull SenderToCallerMapper<CommandSender> self() {
      return caller -> caller;
    }

    @Override
    @NotNull C apply(final @NotNull CommandSender sender);
  }
}
