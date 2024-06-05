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
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.permission.PermissionContainer;
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
    public void reply(final String text) {
        Log.info(text);
    }

    @Override
    public void reply(final Component component) {
        getAudience().sendMessage(component);
    }

    @Override
    public void error(final String text) {
        Log.warn(text);
    }

    @Override
    public void error(final Component component) {
        getAudience().sendWarning(component);
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
        throw new UnsupportedOperationException("Cannot get player from server command sender");
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public PermissionContainer getPermissions() {
        return PermissionContainer.all();
    }

    @Override
    public Audience getAudience() {
        return DistributorProvider.get().getAudienceProvider().getServer();
    }
}
