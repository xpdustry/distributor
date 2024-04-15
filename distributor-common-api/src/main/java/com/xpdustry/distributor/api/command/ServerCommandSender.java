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

import arc.util.Log;
import com.xpdustry.distributor.api.permission.TriState;
import java.util.Locale;
import mindustry.gen.Player;

final class ServerCommandSender implements CommandSender {

    static final ServerCommandSender INSTANCE = new ServerCommandSender();

    private ServerCommandSender() {}

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public void sendMessage(final String text) {
        for (final var line : text.split("\n", -1)) {
            Log.info(line);
        }
    }

    @Override
    public void sendWarning(final String text) {
        for (final var line : text.split("\n", -1)) {
            Log.warn(line);
        }
    }

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

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public TriState getPermission(final String permission) {
        return TriState.TRUE;
    }
}
