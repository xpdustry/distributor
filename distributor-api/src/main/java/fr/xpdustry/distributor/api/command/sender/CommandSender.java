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
package fr.xpdustry.distributor.api.command.sender;

import fr.xpdustry.distributor.api.DistributorProvider;
import java.util.Locale;
import mindustry.gen.Player;

/**
 * Represents an entity that can send commands.
 */
public interface CommandSender {

    /**
     * Wraps a player into a command sender.
     */
    static CommandSender player(final Player player) {
        return new PlayerCommandSender(player);
    }

    /**
     * Returns the console command sender of this server.
     */
    static CommandSender console() {
        return ConsoleCommandSender.INSTANCE;
    }

    /**
     * Sends a simple message to the sender.
     *
     * @param content the message to send
     */
    void sendMessage(final String content);

    /**
     * Sends a localized message to the sender.
     * <br>
     * <strong>Note:</strong> if the string is not found, the key will be sent instead. Such as {@code ???key???}.
     *
     * @param key the key of the message to send
     * @param args the arguments to format the message with
     */
    default void sendLocalizedMessage(final String key, final Object... args) {
        final var format = DistributorProvider.get().getGlobalLocalizationSource().localize(key, this.getLocale());
        this.sendMessage(format == null ? "???" + key + " ???" : format.format(args));
    }

    /**
     * Sends a warning message to the sender.
     *
     * @param content the warning to send
     */
    void sendWarning(final String content);

    /**
     * Sends a localized warning message to the sender.
     * <br>
     * <strong>Note:</strong> if the key is not found, the key will be sent instead. Such as {@code ???key???}.
     *
     * @param key the key of the warning to send
     * @param args the arguments to format the warning with
     */
    default void sendLocalizedWarning(final String key, final Object... args) {
        final var format = DistributorProvider.get().getGlobalLocalizationSource().localize(key, this.getLocale());
        this.sendWarning(format == null ? "???" + key + " ???" : format.format(args));
    }

    /**
     * Returns the locale of this sender.
     */
    Locale getLocale();

    /**
     * @return the player proxied by this sender if it's a player.
     * @throws UnsupportedOperationException if this sender is not a player.
     */
    Player getPlayer();

    /**
     * Returns whether this sender is a player.
     */
    boolean isPlayer();

    /**
     * Returns whether this sender is the console.
     */
    boolean isConsole();
}
