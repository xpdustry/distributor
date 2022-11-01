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

import java.util.Locale;
import mindustry.gen.Player;

public interface CommandSender {

    static CommandSender player(final Player player) {
        return new PlayerCommandSender(player);
    }

    static CommandSender console() {
        return ConsoleCommandSender.INSTANCE;
    }

    void sendMessage(final String content);

    void sendWarning(final String content);

    // void sendSuccess(final String content);

    Locale getLocale();

    Player getPlayer();

    boolean isPlayer();

    boolean isConsole();
}
