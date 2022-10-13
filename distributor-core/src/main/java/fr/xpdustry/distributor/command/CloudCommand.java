package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.command.sender.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

/**
 * This command delegate it's call to it's command manager.
 */
public final class CloudCommand<C> extends CommandHandler.Command {

  private final ArcCommandManager<C> manager;

  CloudCommand(final String name, final String description, final ArcCommandManager<C> manager) {
    super(name, "[args...]", description, new CloudCommandRunner<>(name, manager));
    this.manager = manager;
  }

  public @NotNull ArcCommandManager<C> getManager() {
    return manager;
  }

  private static final class CloudCommandRunner<C> implements CommandHandler.CommandRunner<Player> {

    private final String name;
    private final ArcCommandManager<C> manager;

    private CloudCommandRunner(final String name, ArcCommandManager<C> manager) {
      this.name = name;
      this.manager = manager;
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Override
    public void accept(final String[] args, final @Nullable Player player) {
      final var sender = manager.getSenderToNativeMapper().apply(
        player != null ? CommandSender.player(player) : CommandSender.console()
      );
      final var input = new StringBuilder(name);
      for (final var arg : args) {
        input.append(' ').append(arg);
      }

      manager.executeCommand(sender, input.toString()).whenComplete((result, throwable) -> {
        if (throwable == null) {
          return;
        }
        if (throwable instanceof ArgumentParseException t) {
          throwable = t.getCause();
        }

        if (throwable instanceof InvalidSyntaxException t) {
          manager.handleException(
            sender,
            InvalidSyntaxException.class,
            t,
            (s, e) -> sendException(sender, ArcCaptionKeys.COMMAND_INVALID_SYNTAX, CaptionVariable.of("syntax", e.getCorrectSyntax()))
          );
        } else if (throwable instanceof NoPermissionException t) {
          manager.handleException(
            sender,
            NoPermissionException.class,
            t,
            (s, e) -> sendException(sender, ArcCaptionKeys.COMMAND_INVALID_PERMISSION, CaptionVariable.of("permission", e.getMissingPermission()))
          );
        } else if (throwable instanceof NoSuchCommandException t) {
          manager.handleException(
            sender,
            NoSuchCommandException.class,
            t,
            (s, e) -> sendException(sender, ArcCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND, CaptionVariable.of("command", e.getSuppliedCommand()))
          );
        } else if (throwable instanceof ParserException t) {
          manager.handleException(
            sender,
            ParserException.class,
            t,
            (s, e) -> sendException(sender, e.errorCaption(), e.captionVariables())
          );
        } else if (throwable instanceof CommandExecutionException t) {
          manager.handleException(
            sender,
            CommandExecutionException.class,
            t,
            (s, e) -> sendException(sender, ArcCaptionKeys.COMMAND_FAILURE_EXECUTION, CaptionVariable.of("message", getErrorMessage(e)))
          );
          Log.err(throwable);
        } else {
          sendException(
            sender,
            ArcCaptionKeys.COMMAND_FAILURE_EXECUTION,
            CaptionVariable.of("message", getErrorMessage(throwable))
          );
          Log.err(throwable);
        }
      });
    }

    private void sendException(final C sender, final Caption caption, final CaptionVariable... variables) {
      final var message = manager.captionRegistry().getCaption(caption, sender);
      final var formatted = manager.captionVariableReplacementHandler().replaceVariables(message, variables);
      manager.getNativeToSenderMapper().apply(sender).sendWarning(formatted);
    }

    private String getErrorMessage(final Throwable throwable) {
      return throwable.getMessage() != null ? throwable.getMessage() : "none";
    }
  }
}
