/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.api.command;

import arc.util.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.api.command.sender.*;
import mindustry.gen.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This command delegate it's call to it's command manager.
 */
public final class ArcCommand<C> extends CommandHandler.Command {

  private final ArcCommandManager<C> manager;

  ArcCommand(final String name, final String description, final ArcCommandManager<C> manager) {
    super(name, "[args...]", description, new ArcCommandRunner<>(name, manager));
    this.manager = manager;
  }

  public ArcCommandManager<C> getManager() {
    return manager;
  }

  private static final class ArcCommandRunner<C> implements CommandHandler.CommandRunner<Player> {

    private final String name;
    private final ArcCommandManager<C> manager;

    private ArcCommandRunner(final String name, final ArcCommandManager<C> manager) {
      this.name = name;
      this.manager = manager;
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Override
    public void accept(final String[] args, final @Nullable Player player) {
      final var sender = manager.getCommandSenderMapper()
        .apply(player != null ? CommandSender.player(player) : CommandSender.console());
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
            (s, e) -> sendException(
              sender,
              ArcCaptionKeys.COMMAND_INVALID_SYNTAX,
              CaptionVariable.of("syntax", e.getCorrectSyntax())));
        } else if (throwable instanceof NoPermissionException t) {
          manager.handleException(
            sender,
            NoPermissionException.class,
            t,
            (s, e) -> sendException(
              sender,
              ArcCaptionKeys.COMMAND_INVALID_PERMISSION,
              CaptionVariable.of("permission", e.getMissingPermission())));
        } else if (throwable instanceof NoSuchCommandException t) {
          manager.handleException(
            sender,
            NoSuchCommandException.class,
            t,
            (s, e) -> sendException(
              sender,
              ArcCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND,
              CaptionVariable.of("command", e.getSuppliedCommand())));
        } else if (throwable instanceof ParserException t) {
          manager.handleException(
            sender,
            ParserException.class,
            t,
            (s, e) -> sendException(sender, e.errorCaption(), e.captionVariables()));
        } else if (throwable instanceof CommandExecutionException t) {
          final var temp = throwable;
          manager.handleException(
            sender,
            CommandExecutionException.class,
            t,
            (s, e) -> {
              sendException(
                sender,
                ArcCaptionKeys.COMMAND_FAILURE_EXECUTION,
                CaptionVariable.of("message", getErrorMessage(e)));
              Log.err(temp);
            });
        } else {
          sendException(
            sender,
            ArcCaptionKeys.COMMAND_FAILURE_EXECUTION,
            CaptionVariable.of("message", getErrorMessage(throwable)));
          Log.err(throwable);
        }
      });
    }

    private void sendException(final C sender, final Caption caption, final CaptionVariable... variables) {
      final var message = manager.captionRegistry().getCaption(caption, sender);
      final var formatted = manager.captionVariableReplacementHandler().replaceVariables(message, variables);
      manager.getBackwardsCommandSenderMapper().apply(sender).sendWarning(formatted);
    }

    private String getErrorMessage(final Throwable throwable) {
      return throwable.getMessage() != null ? throwable.getMessage() : "none";
    }
  }
}
