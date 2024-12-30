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
package com.xpdustry.distributor.command.lamp.actor;

import arc.util.Strings;
import com.xpdustry.distributor.api.command.CommandSender;
import com.xpdustry.distributor.api.player.MUUID;
import java.util.UUID;
import revxrsal.commands.Lamp;

final class MindustryCommandActorImpl implements MindustryCommandActor {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private final CommandSender sender;
    private final Lamp<MindustryCommandActor> lamp;

    MindustryCommandActorImpl(final Lamp<MindustryCommandActor> lamp, final CommandSender sender) {
        this.lamp = lamp;
        this.sender = sender;
    }

    @Override
    public CommandSender getCommandSender() {
        return this.sender;
    }

    @Override
    public Lamp<MindustryCommandActor> lamp() {
        return this.lamp;
    }

    @Override
    public String name() {
        return this.sender.getName();
    }

    @Override
    public UUID uniqueId() {
        return this.sender.isServer()
                ? CONSOLE_UUID
                : MUUID.from(this.sender.getPlayer()).toRealUUID();
    }

    @Override
    public void reply(final String message) {
        this.sender.reply(message);
    }

    @Override
    public void sendRawMessage(final String message) {
        this.sender.reply(Strings.stripColors(message));
    }

    @Override
    public void error(final String message) {
        this.sender.error(message);
    }

    @Override
    public void sendRawError(final String message) {
        this.sender.error(Strings.stripColors(message));
    }
}
