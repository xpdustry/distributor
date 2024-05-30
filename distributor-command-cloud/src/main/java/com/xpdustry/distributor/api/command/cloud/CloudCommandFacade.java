/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.api.command.cloud;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.command.CommandElement;
import com.xpdustry.distributor.api.command.CommandFacade;
import com.xpdustry.distributor.api.command.CommandHelp;
import com.xpdustry.distributor.api.command.CommandSender;
import com.xpdustry.distributor.api.command.DescriptionFacade;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.Command;
import org.incendo.cloud.help.HelpQuery;
import org.incendo.cloud.help.result.MultipleCommandResult;
import org.incendo.cloud.help.result.VerboseCommandResult;

/**
 * This special command class delegates its call to its command manager.
 */
final class CloudCommandFacade<C> extends CommandHandler.Command implements CommandFacade {

    final MindustryCommandManager<C> manager;
    private final boolean alias;
    private final String realName;
    private final DescriptionFacade descriptionFacade;

    CloudCommandFacade(
            final String name,
            final DescriptionFacade description,
            final MindustryCommandManager<C> manager,
            final boolean alias,
            final boolean prefixed) {
        super(
                (prefixed ? manager.getPlugin().getMetadata().getName() + ":" : "") + name,
                "[args...]",
                description.getText(),
                new CloudCommandRunner<>(name, manager));
        this.manager = manager;
        this.alias = alias;
        this.realName = name;
        this.descriptionFacade = description;
    }

    @Override
    public String getRealName() {
        return this.realName;
    }

    @Override
    public String getName() {
        return this.text;
    }

    @Override
    public DescriptionFacade getDescription() {
        return descriptionFacade;
    }

    @Override
    public boolean isAlias() {
        return this.alias;
    }

    @Override
    public boolean isVisible(final CommandSender sender) {
        final var mapped = manager.senderMapper().map(sender);
        return manager.commands().stream()
                .anyMatch(command -> command.rootComponent().name().equalsIgnoreCase(realName)
                        && manager.testPermission(mapped, command.commandPermission())
                                .allowed());
    }

    @Override
    public CommandHelp getHelp(final CommandSender sender, final String query) {
        final var mapped = manager.senderMapper().map(sender);
        final var result = manager.createHelpHandler().query(HelpQuery.of(mapped, getRealName() + " " + query));
        if (result instanceof MultipleCommandResult<C> multi) {
            return CommandHelp.Suggestion.of(multi.longestPath(), multi.childSuggestions());
        } else if (result instanceof VerboseCommandResult<C> verbose) {
            final var command = verbose.entry().command();
            return CommandHelp.Entry.of(
                    verbose.entry().syntax(),
                    this.manager
                            .descriptionMapper()
                            .map(command.commandDescription().description()),
                    this.manager
                            .descriptionMapper()
                            .map(command.commandDescription().verboseDescription()),
                    getArguments(command),
                    getFlags(command));
        } else {
            return CommandHelp.Empty.of();
        }
    }

    private List<CommandElement.Argument> getArguments(final Command<C> command) {
        return command.nonFlagArguments().stream()
                .map(component -> CommandElement.Argument.of(
                        component.name(),
                        this.manager.descriptionMapper().map(component.description()),
                        Set.copyOf(component.alternativeAliases()),
                        switch (component.type()) {
                            case LITERAL -> CommandElement.Argument.Kind.LITERAL;
                            case REQUIRED_VARIABLE -> CommandElement.Argument.Kind.REQUIRED;
                            case OPTIONAL_VARIABLE -> CommandElement.Argument.Kind.OPTIONAL;
                            case FLAG -> throw new IllegalStateException("impossible");
                        }))
                .toList();
    }

    private List<CommandElement.Flag> getFlags(final Command<C> command) {
        final var flags = new ArrayList<CommandElement.Flag>();
        final var parser = command.flagParser();
        if (parser != null) {
            for (final var flag : parser.flags()) {
                flags.add(CommandElement.Flag.of(
                        flag.name(),
                        this.manager.descriptionMapper().map(flag.description()),
                        Set.copyOf(flag.aliases()),
                        CommandElement.Flag.Kind.OPTIONAL,
                        switch (flag.mode()) {
                            case SINGLE -> CommandElement.Flag.Mode.SINGLE;
                            case REPEATABLE -> CommandElement.Flag.Mode.REPEATABLE;
                        }));
            }
        }
        return flags;
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.manager.getPlugin();
    }

    private record CloudCommandRunner<C>(String name, MindustryCommandManager<C> manager)
            implements CommandHandler.CommandRunner<Player> {

        @SuppressWarnings("FutureReturnValueIgnored")
        @Override
        public void accept(final String[] args, final @Nullable Player player) {
            final var sender = this.manager
                    .senderMapper()
                    .map(player != null ? CommandSender.player(player) : CommandSender.server());

            final var input = new StringBuilder(this.name);
            for (final var arg : args) {
                input.append(' ').append(arg);
            }

            this.manager.commandExecutor().executeCommand(sender, input.toString());
        }
    }
}
