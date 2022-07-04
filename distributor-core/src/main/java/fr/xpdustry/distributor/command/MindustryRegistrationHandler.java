package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.internal.*;

import cloud.commandframework.meta.*;
import mindustry.gen.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

/**
 * This class acts as a bridge between the {@link MindustryCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as native arc commands.
 */
public final class MindustryRegistrationHandler<C> implements CommandRegistrationHandler {

  private static final String DEFAULT_ARGS = "[args...]";

  private final MindustryCommandManager<C> manager;
  private final CommandHandler handler;

  public MindustryRegistrationHandler(final @NotNull MindustryCommandManager<C> manager, final @NotNull CommandHandler handler) {
    this.manager = manager;
    this.handler = handler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean registerCommand(final @NotNull Command<?> command) {
    final var root = (StaticArgument<?>) command.getArguments().get(0);

    if (manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)) {
      root.getAliases().forEach(handler::removeCommand);
    }

    var added = false;
    final var commands = getNativeCommands();

    for (final var alias : root.getAliases()) {
      if (!commands.containsKey(alias)) {
        // TODO CLean this shit up
        final var cmd = new CloudCommand(alias, (Command<C>) command);
        commands.put(alias, cmd);
        handler.getCommandList().add(cmd);
        added = true;
      }
    }

    return added;
  }

  @Override
  public void unregisterRootCommand(final @NonNull StaticArgument<?> root) {
    final var commands = getNativeCommands();
    root.getAliases().stream()
      .map(commands::get)
      .filter(CloudCommand.class::isInstance)
      .map(c -> c.text)
      .forEach(handler::removeCommand);
  }

  private ObjectMap<String, CommandHandler.Command> getNativeCommands() {
    return Reflect.get(handler, "commands");
  }

  /**
   * This command delegate it's call to it's command manager.
   */
  public final class CloudCommand extends CommandHandler.Command {

    private final Command<C> command;

    private CloudCommand(final @NotNull String name, final @NotNull Command<C> command) {
      super(name, DEFAULT_ARGS, command.getCommandMeta().getOrDefault(CommandMeta.DESCRIPTION, ""), new CloudCommandRunner(name));
      this.command = command;
    }

    public @NotNull Command<C> getWrappedCommand() {
      return command;
    }
  }

  private final class CloudCommandRunner implements CommandHandler.CommandRunner<Player> {

    private final String name;

    private CloudCommandRunner(final @NotNull String name) {
      this.name = name;
    }

    @Override
    public void accept(final @NotNull String[] args, final @Nullable Player player) {
      // TODO figure out how to get a OnlinePlayer instance here...
      final var caller = manager.getSenderToCallerMapper().apply(new ConsoleCommandSender());
      final var input = new StringBuilder(name);
      for (final var arg : args) input.append(arg);

      manager.executeCommand(caller, input.toString()).whenComplete((result, throwable) -> {
        if (throwable == null) {
          return;
        } else if (throwable instanceof ArgumentParseException t) {
          throwable = t.getCause();
        }

        if (throwable instanceof InvalidSyntaxException t) {
          manager.handleException(caller, InvalidSyntaxException.class, t, (c, e) -> {
            sendException(c, MindustryCaptionKeys.COMMAND_INVALID_SYNTAX, CaptionVariable.of("syntax", e.getCorrectSyntax()));
          });
        } else if (throwable instanceof NoPermissionException t) {
          manager.handleException(caller, NoPermissionException.class, t, (c, e) -> {
            sendException(c, MindustryCaptionKeys.COMMAND_INVALID_PERMISSION, CaptionVariable.of("permission", e.getMissingPermission()));
          });
        } else if (throwable instanceof NoSuchCommandException t) {
          manager.handleException(caller, NoSuchCommandException.class, t, (c, e) -> {
            sendException(c, MindustryCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND, CaptionVariable.of("command", e.getSuppliedCommand()));
          });
        } else if (throwable instanceof ParserException t) {
          manager.handleException(caller, ParserException.class, t, (c, e) -> {
            sendException(c, e.errorCaption(), e.captionVariables());
          });
        } else if (throwable instanceof CommandExecutionException t) {
          manager.handleException(caller, CommandExecutionException.class, t, (c, e) -> {
            sendException(c, MindustryCaptionKeys.COMMAND_FAILURE_EXECUTION, CaptionVariable.of("message", e.getCause().getMessage()));
          });
        } else {
          sendException(caller, MindustryCaptionKeys.COMMAND_FAILURE_UNKNOWN, CaptionVariable.of("message", throwable.getMessage()));
        }
      });
    }

    private void sendException(final @NotNull C caller, final Caption caption, final CaptionVariable... variables) {
      final var message = manager.captionRegistry().getCaption(caption, caller);
      final var formatted = manager.captionVariableReplacementHandler().replaceVariables(message, variables);
      manager.getCallerToSenderMapper().apply(caller).sendMessage(formatted);
    }
  }
}
