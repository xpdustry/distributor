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

import com.xpdustry.distributor.api.permission.PermissionHolder;
import com.xpdustry.distributor.api.translation.LocaleHolder;
import mindustry.gen.Player;

public interface CommandSender extends PermissionHolder, LocaleHolder {

    static CommandSender player(final Player player) {
        return new PlayerCommandSender(player);
    }

    static CommandSender server() {
        return ServerCommandSender.INSTANCE;
    }

    String getName();

    void sendWarning(final String text);

    void sendMessage(final String text);

    boolean isPlayer();

    boolean isServer();

    Player getPlayer();
}
