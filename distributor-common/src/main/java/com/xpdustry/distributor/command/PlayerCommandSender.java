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
package com.xpdustry.distributor.command;

import java.util.Locale;
import mindustry.gen.Player;

record PlayerCommandSender(Player player) implements CommandSender {

    @Override
    public void sendMessage(final String text) {
        this.player.sendMessage(text);
    }

    @Override
    public void sendWarning(final String text) {
        this.player.sendMessage("[red]" + text);
    }

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
        return this.player;
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(this.player.locale().replace('_', '-'));
    }
}