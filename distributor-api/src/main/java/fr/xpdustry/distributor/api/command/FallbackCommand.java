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
import arc.util.CommandHandler.Command;
import arc.util.CommandHandler.CommandRunner;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This allows to register commands with colliding names in a super command.
 */
public final class FallbackCommand extends CommandHandler.Command {

    private final Map<String, CommandHandler.Command> commands;

    public FallbackCommand(final MindustryPlugin plugin) {
        this(plugin, new HashMap<>());
    }

    private FallbackCommand(final MindustryPlugin plugin, final Map<String, CommandHandler.Command> commands) {
        super(
                plugin.getDescriptor().getName(),
                "[command] [args...]",
                "Commands of " + plugin.getDescriptor().getDisplayName(),
                new FallbackCommandRunner(commands));
        this.commands = commands;
    }

    public void addCommand(final CommandHandler.Command command) {
        this.commands.put(command.text, command);
    }

    public void removeCommand(final String command) {
        this.commands.remove(command);
    }

    public List<Command> getCommandList() {
        return new ArrayList<>(this.commands.values());
    }

    private static final class FallbackCommandRunner implements CommandHandler.CommandRunner<Player> {

        private static final Field COMMAND_RUNNER_FIELD;

        static {
            try {
                COMMAND_RUNNER_FIELD = CommandHandler.Command.class.getDeclaredField("runner");
                COMMAND_RUNNER_FIELD.setAccessible(true);
            } catch (final NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        private final Map<String, CommandHandler.Command> commands;

        private FallbackCommandRunner(final Map<String, CommandHandler.Command> commands) {
            this.commands = commands;
        }

        @Override
        public void accept(final String[] args, final @Nullable Player player) {
            final var sender = player == null ? CommandSender.console() : CommandSender.player(player);

            if (args.length == 0) {
                sender.sendMessage("Available commands:");
                for (final var command : this.commands.values()) {
                    sender.sendMessage(" - " + command.text + " " + command.paramText + ": " + command.description);
                }
                return;
            }

            if (!this.commands.containsKey(args[0])) {
                sender.sendMessage("Unknown command: " + args[0]);
                return;
            }

            try {
                @SuppressWarnings("unchecked")
                final var runner = (CommandRunner<Player>) COMMAND_RUNNER_FIELD.get(this.commands.get(args[0]));
                final var newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                runner.accept(newArgs, player);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
