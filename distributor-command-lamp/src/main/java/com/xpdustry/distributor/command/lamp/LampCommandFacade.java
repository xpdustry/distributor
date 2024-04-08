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
import com.xpdustry.distributor.command.CommandFacade;
import com.xpdustry.distributor.command.CommandHelp;
import com.xpdustry.distributor.command.CommandSender;
import com.xpdustry.distributor.command.DescriptionFacade;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import java.util.Map;
import mindustry.gen.Player;
import org.jspecify.annotations.Nullable;
import revxrsal.commands.core.CommandPath;

final class LampCommandFacade extends CommandHandler.Command implements CommandFacade {

    final MindustryCommandHandler handler;
    private final String name;
    private final DescriptionFacade description;
    private final boolean alias;
    private final boolean prefixed;

    LampCommandFacade(
            final MindustryCommandHandler handler,
            final String name,
            final DescriptionFacade description,
            final boolean alias,
            final boolean prefixed) {
        super(
                prefixed ? handler.getPlugin().getMetadata().getName() + ":" + name : name,
                "[args...]",
                description.getText(),
                new LampCommandRunner(name, handler));
        this.handler = handler;
        this.name = name;
        this.description = description;
        this.alias = alias;
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
        return this.description;
    }

    @Override
    public boolean isAlias() {
        return this.alias;
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
        return CommandHelp.Empty.getInstance();
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

            final var input = new StringBuilder(this.name);
            for (final var arg : args) {
                input.append(' ').append(arg);
            }

            try {
                this.handler.dispatch(actor, input.toString());
            } catch (final Throwable throwable) {
                this.handler.getExceptionHandler().handleException(throwable, actor);
            }
        }
    }
}
