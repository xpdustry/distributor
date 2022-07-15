package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.CommandHandler;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.internal.*;

import fr.xpdustry.distributor.DistributorPlugin;
import fr.xpdustry.distributor.text.Component;
import fr.xpdustry.distributor.text.format.TextColor;
import mindustry.gen.*;

import java.lang.reflect.Field;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This class acts as a bridge between the {@link MindustryCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as native arc commands.
 */
public final class MindustryRegistrationHandler<C> implements CommandRegistrationHandler {

  private static final String DEFAULT_ARGS = "[args...]";
  private static final Field COMMAND_MAP_ACCESSOR;

  static {
    try {
      COMMAND_MAP_ACCESSOR = CommandHandler.class.getDeclaredField("commands");
      COMMAND_MAP_ACCESSOR.setAccessible(true);
    } catch (final NoSuchFieldException e) {
      throw new RuntimeException("Unable to access CommandHandler#commands.", e);
    }
  }

  private final MindustryCommandManager<C> manager;
  private final CommandHandler handler;
  private final ObjectMap<String, CommandHandler.Command> commands;

  @SuppressWarnings("unchecked")
  public MindustryRegistrationHandler(final MindustryCommandManager<C> manager, final CommandHandler handler) {
    this.manager = manager;
    this.handler = handler;
    try {
      this.commands = (ObjectMap<String, CommandHandler.Command>) COMMAND_MAP_ACCESSOR.get(handler);
    } catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean registerCommand(final Command<?> command) {
    final var root = (StaticArgument<?>) command.getArguments().get(0);

    if (manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)) {
      root.getAliases().forEach(handler::removeCommand);
    }

    var added = false;

    for (final var alias : root.getAliases()) {
      if (!commands.containsKey(alias)) {
        final var nativeCommand = new CloudCommand(alias);
        commands.put(alias, nativeCommand);
        handler.getCommandList().add(nativeCommand);
        added = true;
      }
    }

    return added;
  }

  @Override
  public void unregisterRootCommand(final StaticArgument<?> root) {
    root.getAliases().stream()
      .map(commands::get)
      .filter(CloudCommand.class::isInstance)
      .map(c -> c.text) // Name
      .forEach(handler::removeCommand);
  }

  /**
   * This command delegate it's call to it's command manager.
   */
  public final class CloudCommand extends CommandHandler.Command {

    private CloudCommand(final String name) {
      super(name, DEFAULT_ARGS, "", new CloudCommandRunner(name));
    }
  }

  private final class CloudCommandRunner implements CommandHandler.CommandRunner<Player> {

    private final String name;

    private CloudCommandRunner(final String name) {
      this.name = name;
    }

    @Override
    public void accept(final String[] args, final @Nullable Player player) {
      final var provider = DistributorPlugin.getAudienceProvider();
      // TODO, Fix player audience
      final var audience = player != null ? provider.player(player.uuid()) : provider.console();
      final var sender = manager.getAudienceToSenderMapper().apply(audience);
      
      final var input = new StringBuilder(name);
      for (final var arg : args) {
        input.append(' ').append(arg);
      }

      manager.executeCommand(sender, input.toString()).whenComplete((result, throwable) -> {
        if (throwable == null) {
          return;
        } else if (throwable instanceof ArgumentParseException t) {
          throwable = t.getCause();
        }

        if (throwable instanceof InvalidSyntaxException t) {
          manager.handleException(sender, InvalidSyntaxException.class, t, (s, e) -> sendException(
            sender,
            MindustryCaptionKeys.COMMAND_INVALID_SYNTAX,
            CaptionVariable.of("syntax", e.getCorrectSyntax())
          ));
        } else if (throwable instanceof NoPermissionException t) {
          manager.handleException(sender, NoPermissionException.class, t, (s, e) -> sendException(
            sender,
            MindustryCaptionKeys.COMMAND_INVALID_PERMISSION,
            CaptionVariable.of("permission", e.getMissingPermission())
          ));
        } else if (throwable instanceof NoSuchCommandException t) {
          manager.handleException(sender, NoSuchCommandException.class, t, (s, e) -> sendException(
            sender,
            MindustryCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND,
            CaptionVariable.of("command", e.getSuppliedCommand())
          ));
        } else if (throwable instanceof ParserException t) {
          manager.handleException(sender, ParserException.class, t, (s, e) -> sendException(
            sender, e.errorCaption(), e.captionVariables()
          ));
        } else if (throwable instanceof CommandExecutionException t) {
          manager.handleException(sender, CommandExecutionException.class, t, (s, e) -> sendException(
            sender,
            MindustryCaptionKeys.COMMAND_FAILURE_EXECUTION,
            CaptionVariable.of("message", e.getCause().getMessage())
          ));
        } else {
          sendException(
            sender,
            MindustryCaptionKeys.COMMAND_FAILURE_UNKNOWN,
            CaptionVariable.of("message", throwable.getMessage())
          );
        }
      });
    }

    private void sendException(final C sender, final Caption caption, final CaptionVariable... variables) {
      final var message = manager.captionRegistry().getCaption(caption, sender);
      final var formatted = manager.captionVariableReplacementHandler().replaceVariables(message, variables);
      manager.getSenderToAudienceMapper()
        .apply(sender)
        .sendMessage(Component.text(formatted, TextColor.RED));
    }
  }
}
