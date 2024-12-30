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
package com.xpdustry.distributor.command.lamp.hooks;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.command.CommandElement;
import com.xpdustry.distributor.api.command.CommandFacade;
import com.xpdustry.distributor.api.command.CommandHelp;
import com.xpdustry.distributor.api.command.CommandSender;
import com.xpdustry.distributor.api.command.DescriptionFacade;
import com.xpdustry.distributor.api.command.DescriptionMapper;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.command.lamp.LampDescribable;
import com.xpdustry.distributor.command.lamp.actor.ActorMapper;
import com.xpdustry.distributor.command.lamp.actor.MindustryCommandActor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revxrsal.commands.Lamp;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.node.CommandNode;

final class LampCommandFacade<A extends MindustryCommandActor> extends CommandHandler.Command implements CommandFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LampCommandFacade.class);

    private final MindustryPlugin plugin;
    private final String name;
    private final DescriptionFacade descriptionFacade;
    private final Lamp<A> lamp;
    private final ActorMapper<A> actorMapper;
    private final DescriptionMapper<LampDescribable<A>> descriptionMapper;

    LampCommandFacade(
            final MindustryPlugin plugin,
            final String name,
            final DescriptionFacade descriptionFacade,
            final Lamp<A> lamp,
            final ActorMapper<A> actorMapper,
            final DescriptionMapper<LampDescribable<A>> descriptionMapper,
            final boolean prefixed) {
        super(
                prefixed ? plugin.getMetadata().getName() + ":" + name : name,
                "[args...]",
                descriptionFacade.getText(),
                new LampCommandRunner<>(name, lamp, actorMapper));
        this.plugin = plugin;
        this.name = name;
        this.descriptionFacade = descriptionFacade;
        this.lamp = lamp;
        this.actorMapper = actorMapper;
        this.descriptionMapper = descriptionMapper;
    }

    @Override
    public String getRealName() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.text;
    }

    @Override
    public DescriptionFacade getDescription() {
        return this.descriptionFacade;
    }

    @Override
    public boolean isAlias() {
        return false;
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean isVisible(final CommandSender sender) {
        final var actor = actorMapper.toActor(sender, lamp);
        return this.lamp.registry().commands().stream()
                .filter(command -> command.firstNode().name().equalsIgnoreCase(this.name))
                .anyMatch(command -> command.permission().isExecutableBy(actor));
    }

    @Override
    public CommandHelp getHelp(final CommandSender sender, final String query) {
        final A actor = actorMapper.toActor(sender, lamp);
        final var base = new ArrayList<String>();
        base.add(this.getRealName().toLowerCase(Locale.ROOT));
        if (!query.isBlank()) {
            base.addAll(Arrays.asList(query.toLowerCase(Locale.ROOT).trim().split("\\s", -1)));
        }

        final var result = new ArrayList<ExecutableCommand<A>>();
        for (final var command : this.lamp.registry().commands()) {
            if (isParentPath(base, getPath(command))
                    && command.permission().isExecutableBy(actor)
                    && command.parameters().values().stream()
                            .allMatch(p -> p.isOptional() || p.permission().isExecutableBy(actor))) {
                result.add(command);
            }
        }

        if (result.isEmpty()) {
            return CommandHelp.Empty.of();
        }

        if (result.size() == 1) {
            final var command = result.get(0);
            final var visible = command.parameters().values().stream()
                    .filter(p -> p.permission().isExecutableBy(actor))
                    .toList();

            final var arguments = visible.stream()
                    .filter(p -> !(p.isFlag() || p.isSwitch()))
                    .map(p -> CommandElement.Argument.of(
                            p.name(),
                            this.descriptionMapper.map(LampDescribable.of(command, p)),
                            Set.of(),
                            p.isLiteral()
                                    ? CommandElement.Argument.Kind.LITERAL
                                    : p.isOptional()
                                            ? CommandElement.Argument.Kind.OPTIONAL
                                            : CommandElement.Argument.Kind.REQUIRED))
                    .toList();

            final var flags = visible.stream()
                    .filter(p -> p.isFlag() || p.isSwitch())
                    .map(p -> CommandElement.Flag.of(
                            p.name(),
                            descriptionMapper.map(LampDescribable.of(command, p)),
                            Set.of(String.valueOf(p.shorthand())),
                            p.isOptional() ? CommandElement.Flag.Kind.OPTIONAL : CommandElement.Flag.Kind.REQUIRED,
                            CommandElement.Flag.Mode.SINGLE))
                    .toList();

            return CommandHelp.Entry.of(
                    command.usage(),
                    descriptionMapper.map(LampDescribable.of(command)),
                    DescriptionFacade.EMPTY,
                    arguments,
                    flags);
        }

        final var sorted = result.stream()
                .map(this::getPath)
                .sorted(Comparator.comparingInt(List::size))
                .toList();
        final String prefix;
        if (sorted.get(0).size() == sorted.get(1).size()) {
            final var copy = new ArrayList<>(sorted.get(0));
            copy.remove(copy.size() - 1);
            prefix = String.join(" ", copy);
        } else {
            prefix = String.join(" ", sorted.get(0));
        }
        return CommandHelp.Suggestion.of(
                prefix, sorted.stream().map(path -> String.join(" ", path)).toList());
    }

    private boolean isParentPath(final List<String> source, final List<String> target) {
        if (target.size() < source.size()) {
            return false;
        }
        for (int i = 0; i < source.size(); i++) {
            if (!source.get(i).startsWith(target.get(i))) {
                return false;
            }
        }
        return true;
    }

    private List<String> getPath(final ExecutableCommand<A> command) {
        return command.nodes().stream()
                .filter(CommandNode::isLiteral)
                .map(CommandNode::name)
                .toList();
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class LampCommandRunner<A extends MindustryCommandActor>
            implements CommandHandler.CommandRunner<Player> {

        private final String name;
        private final Lamp<A> lamp;
        private final ActorMapper<A> mapper;

        private LampCommandRunner(final String name, final Lamp<A> lamp, final ActorMapper<A> mapper) {
            this.name = name;
            this.lamp = lamp;
            this.mapper = mapper;
        }

        @Override
        public void accept(final String[] args, final @Nullable Player player) {
            final var actor =
                    mapper.toActor(player != null ? CommandSender.player(player) : CommandSender.server(), lamp);

            final var input = new StringBuilder();
            input.append(this.name);
            for (final var arg : args) input.append(' ').append(arg);

            try {
                this.lamp.dispatch(actor, input.toString());
            } catch (final Throwable throwable) {
                LOGGER.error("An unexpected error occurred while executing a command.", throwable);
            }
        }
    }
}
