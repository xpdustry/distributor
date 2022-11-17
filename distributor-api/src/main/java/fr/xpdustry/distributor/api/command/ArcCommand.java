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

import arc.util.CommandHandler;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.exceptions.NoSuchCommandException;
import cloud.commandframework.exceptions.parsing.ParserException;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This special command handler class delegates it's call to it's command manager.
 */
public final class ArcCommand<C> extends CommandHandler.Command {

    private final ArcCommandManager<C> manager;

    ArcCommand(final String name, final String description, final ArcCommandManager<C> manager) {
        super(name, "[args...]", description, new ArcCommandRunner<>(name, manager));
        this.manager = manager;
    }

    public ArcCommandManager<C> getManager() {
        return this.manager;
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
            final var sender = this.manager
                    .getCommandSenderMapper()
                    .apply(player != null ? CommandSender.player(player) : CommandSender.console());
            final var input = new StringBuilder(this.name);
            for (final var arg : args) {
                input.append(' ').append(arg);
            }

            this.manager.executeCommand(sender, input.toString()).whenComplete((result, throwable) -> {
                if (throwable == null) {
                    return;
                }
                if (throwable instanceof ArgumentParseException t) {
                    throwable = t.getCause();
                }

                if (throwable instanceof InvalidSyntaxException t) {
                    this.manager.handleException(
                            sender,
                            InvalidSyntaxException.class,
                            t,
                            (s, e) -> this.sendException(
                                    sender,
                                    ArcCaptionKeys.COMMAND_INVALID_SYNTAX,
                                    CaptionVariable.of("syntax", e.getCorrectSyntax())));
                } else if (throwable instanceof NoPermissionException t) {
                    this.manager.handleException(
                            sender,
                            NoPermissionException.class,
                            t,
                            (s, e) -> this.sendException(
                                    sender,
                                    ArcCaptionKeys.COMMAND_INVALID_PERMISSION,
                                    CaptionVariable.of("permission", e.getMissingPermission())));
                } else if (throwable instanceof NoSuchCommandException t) {
                    this.manager.handleException(
                            sender,
                            NoSuchCommandException.class,
                            t,
                            (s, e) -> this.sendException(
                                    sender,
                                    ArcCaptionKeys.COMMAND_FAILURE_NO_SUCH_COMMAND,
                                    CaptionVariable.of("command", e.getSuppliedCommand())));
                } else if (throwable instanceof ParserException t) {
                    this.manager.handleException(
                            sender,
                            ParserException.class,
                            t,
                            (s, e) -> this.sendException(sender, e.errorCaption(), e.captionVariables()));
                } else if (throwable instanceof CommandExecutionException t) {
                    this.manager.handleException(
                            sender,
                            CommandExecutionException.class,
                            t,
                            (s, e) -> this.sendException(
                                    sender,
                                    ArcCaptionKeys.COMMAND_FAILURE_EXECUTION,
                                    CaptionVariable.of("message", this.getErrorMessage(e))));
                    this.manager
                            .getPlugin()
                            .getLogger()
                            .error("An error occurred while executing a command.", throwable);
                } else {
                    this.sendException(
                            sender,
                            ArcCaptionKeys.COMMAND_FAILURE_EXECUTION,
                            CaptionVariable.of("message", this.getErrorMessage(throwable)));
                    this.manager
                            .getPlugin()
                            .getLogger()
                            .error("An unexpected error occurred while executing a command.", throwable);
                }
            });
        }

        private void sendException(final C sender, final Caption caption, final CaptionVariable... variables) {
            final var message = this.manager.captionRegistry().getCaption(caption, sender);
            final var formatted =
                    this.manager.captionVariableReplacementHandler().replaceVariables(message, variables);
            this.manager.getBackwardsCommandSenderMapper().apply(sender).sendWarning(formatted);
        }

        private String getErrorMessage(final Throwable throwable) {
            return throwable.getMessage() != null ? throwable.getMessage() : "none";
        }
    }
}
