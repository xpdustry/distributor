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
package com.xpdustry.distributor.core.command;

import mindustry.gen.Player;

public interface CommandSender {

    default void sendWarning(final String text) {}

    default void sendMessage(final String text) {}

    static CommandSender player(final Player player) {
        return new CommandSender() {
            @Override
            public boolean isPlayer() {
                return true;
            }

            @Override
            public boolean isServer() {
                return false;
            }

            @Override
            public Player getPlayer() {
                throw new UnsupportedOperationException();
            }
        };
    }

    static CommandSender server() {
        return new CommandSender() {
            @Override
            public boolean isPlayer() {
                return false;
            }

            @Override
            public boolean isServer() {
                return true;
            }

            @Override
            public Player getPlayer() {
                throw new UnsupportedOperationException();
            }
        };
    }

    boolean isPlayer();

    boolean isServer();

    Player getPlayer();
}
