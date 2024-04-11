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
package com.xpdustry.distributor.command.lamp;

import arc.util.CommandHandler;
import com.xpdustry.distributor.command.CommandElement;
import com.xpdustry.distributor.command.CommandFacade;
import com.xpdustry.distributor.command.CommandHelp;
import com.xpdustry.distributor.command.CommandSender;
import com.xpdustry.distributor.command.DescriptionFacade;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mindustry.gen.Player;
import org.jspecify.annotations.Nullable;
import revxrsal.commands.command.ArgumentStack;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.core.CommandPath;

final class LampCommandFacade extends CommandHandler.Command implements CommandFacade {

    final MindustryCommandHandler handler;
    private final String name;
    private final DescriptionFacade descriptionFacade;
    private final boolean prefixed;

    LampCommandFacade(
            final MindustryCommandHandler handler,
            final String name,
            final DescriptionFacade descriptionFacade,
            final boolean prefixed) {
        super(
                prefixed ? handler.getPlugin().getMetadata().getName() + ":" + name : name,
                "[args...]",
                descriptionFacade.getText(),
                new LampCommandRunner(name, handler));
        this.handler = handler;
        this.name = name;
        this.descriptionFacade = descriptionFacade;
        this.prefixed = prefixed;
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
    public boolean isPrefixed() {
        return this.prefixed;
    }

    @Override
    public boolean isVisible(final CommandSender sender) {
        final var actor = this.handler.wrap(sender);
        final var root = CommandPath.get(this.name);
        return this.handler.getCommands().entrySet().stream()
                .filter(entry -> entry.getKey().isChildOf(root))
                .map(Map.Entry::getValue)
                .anyMatch(command -> command.hasPermission(actor));
    }

    @Override
    public CommandHelp getHelp(final CommandSender sender, final String query) {
        final var actor = this.handler.wrap(sender);
        final var base = new ArrayList<String>();
        base.add(this.getRealName().toLowerCase(Locale.ROOT));
        if (!query.isBlank()) {
            base.addAll(Arrays.asList(query.toLowerCase(Locale.ROOT).trim().split("\\s", -1)));
        }

        final var result = new ArrayList<ExecutableCommand>();
        for (final var entry : this.handler.getCommands().entrySet()) {
            final var path = entry.getKey().toList();
            if (isParentPath(base, path)
                    && entry.getValue().hasPermission(actor)
                    && entry.getValue().getParameters().stream()
                            .allMatch(p -> p.isOptional() || p.hasPermission(actor))) {
                result.add(entry.getValue());
            }
        }

        if (result.isEmpty()) {
            return CommandHelp.Empty.getInstance();
        }

        if (result.size() == 1) {
            final var command = result.get(0);
            final var arguments = new ArrayList<CommandElement.Argument>();
            for (final var literal : command.getPath()) {
                arguments.add(CommandElement.Argument.of(
                        literal, DescriptionFacade.EMPTY, List.of(), CommandElement.Argument.Kind.LITERAL));
            }
            final var visible = command.getParameters().stream()
                    .filter(p -> p.hasPermission(actor))
                    .toList();
            visible.stream()
                    .filter(p -> !(p.isFlag() || p.isSwitch()))
                    .map(p -> CommandElement.Argument.of(
                            p.getName(),
                            this.handler.getDescriptionMapper().map(LampDescribable.Parameter.of(command, p)),
                            List.of(),
                            p.isOptional()
                                    ? CommandElement.Argument.Kind.OPTIONAL
                                    : CommandElement.Argument.Kind.REQUIRED))
                    .forEach(arguments::add);
            return CommandHelp.Entry.of(
                    command.getUsage(),
                    this.handler.getDescriptionMapper().map(LampDescribable.Command.of(command)),
                    DescriptionFacade.EMPTY,
                    arguments,
                    visible.stream()
                            .filter(p -> p.isFlag() || p.isSwitch())
                            .map(p -> CommandElement.Flag.of(
                                    p.getName(),
                                    this.handler.getDescriptionMapper().map(LampDescribable.Parameter.of(command, p)),
                                    List.of(p.getFlagName()),
                                    p.isOptional()
                                            ? CommandElement.Flag.Kind.OPTIONAL
                                            : CommandElement.Flag.Kind.REQUIRED,
                                    CommandElement.Flag.Mode.SINGLE))
                            .toList());
        }

        final var sorted = result.stream()
                .map(ExecutableCommand::getPath)
                .sorted(Comparator.comparingInt(CommandPath::size))
                .toList();
        final String prefix;
        if (sorted.get(0).size() == sorted.get(1).size()) {
            final var mutable = sorted.get(0).toMutablePath();
            mutable.removeLast();
            prefix = mutable.toRealString();
        } else {
            prefix = sorted.get(0).toRealString();
        }
        return CommandHelp.Suggestion.of(
                prefix, sorted.stream().map(CommandPath::toRealString).toList());
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

    @Override
    public MindustryPlugin getPlugin() {
        return this.handler.getPlugin();
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class LampCommandRunner implements CommandHandler.CommandRunner<Player> {

        private final String name;
        private final MindustryCommandHandler handler;

        private LampCommandRunner(final String name, final MindustryCommandHandler handler) {
            this.name = name;
            this.handler = handler;
        }

        @Override
        public void accept(final String[] args, final @Nullable Player player) {
            final var actor = this.handler.wrap(player != null ? CommandSender.player(player) : CommandSender.server());

            final var stack = ArgumentStack.parse(args);
            stack.addFirst(this.name);

            try {
                this.handler.dispatch(actor, stack);
            } catch (final Throwable throwable) {
                this.handler.getExceptionHandler().handleException(throwable, actor);
            }
        }
    }
}
