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
package com.xpdustry.distributor.api.command;

import com.xpdustry.distributor.api.permission.TriState;
import java.util.Locale;
import mindustry.gen.Player;

/**
 * Represents an entity that can send commands.
 */
public interface CommandSender {

    /**
     * Wraps a player into a command sender.
     *
     * @param player the player to wrap
     * @return the player command sender
     */
    static CommandSender player(final Player player) {
        return new PlayerCommandSender(player);
    }

    /**
     * Returns the server itself as a command sender.
     */
    static CommandSender server() {
        return ServerCommandSender.INSTANCE;
    }

    /**
     * Returns the name of this command sender.
     */
    String getName();

    /**
     * Sends a simple message to the sender.
     *
     * @param text the message to send
     */
    void reply(final String text);

    /**
     * Sends a error message to the sender.
     *
     * @param text the message to send
     */
    void error(final String text);

    /**
     * Returns whether this sender is a player.
     */
    boolean isPlayer();

    /**
     * Returns whether this sender is the server.
     */
    boolean isServer();

    /**
     * Returns the player of this command sender.
     *
     * @return the player of this command sender
     * @throws UnsupportedOperationException if this sender is not a player
     */
    Player getPlayer();

    /**
     * Returns the permission state of the given permission.
     *
     * @param permission the permission to check
     * @return the permission state
     */
    TriState getPermission(final String permission);

    /**
     * Returns the locale of this command sender.
     */
    Locale getLocale();
}
