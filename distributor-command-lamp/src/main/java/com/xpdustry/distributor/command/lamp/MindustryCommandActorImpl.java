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

import com.xpdustry.distributor.command.CommandSender;
import com.xpdustry.distributor.player.MUUID;
import java.util.Locale;
import java.util.UUID;
import revxrsal.commands.CommandHandler;

final class MindustryCommandActorImpl implements MindustryCommandActor {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private final MindustryCommandHandler handler;
    private final CommandSender sender;

    MindustryCommandActorImpl(final MindustryCommandHandler handler, final CommandSender sender) {
        this.handler = handler;
        this.sender = sender;
    }

    @Override
    public CommandSender getCommandSender() {
        return this.sender;
    }

    @Override
    public String getName() {
        return this.sender.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.sender.isServer()
                ? CONSOLE_UUID
                : MUUID.from(this.sender.getPlayer()).toRealUUID();
    }

    @Override
    public void reply(final String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public void error(final String message) {
        this.sender.sendWarning(message);
    }

    @Override
    public CommandHandler getCommandHandler() {
        return this.handler;
    }

    @Override
    public Locale getLocale() {
        return this.sender.getLocale();
    }
}
