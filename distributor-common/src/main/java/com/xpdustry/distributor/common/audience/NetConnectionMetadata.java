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
package com.xpdustry.distributor.common.audience;

import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.player.MUUID;
import java.util.Locale;
import mindustry.net.NetConnection;
import mindustry.net.Packets;
import org.checkerframework.checker.nullness.qual.Nullable;

public record NetConnectionMetadata(
        @Nullable MUUID muuid, ComponentColor color, @Nullable String name, @Nullable Locale locale) {
    public static NetConnectionMetadata from(final Packets.ConnectPacket packet) {
        final var muuid = (packet.uuid == null || packet.usid == null) ? null : MUUID.of(packet.uuid, packet.usid);
        final var color = ComponentColor.rgb(packet.color);
        final var name = packet.name;
        final var locale = packet.locale == null ? null : Locale.forLanguageTag(packet.locale.replace("_", "-"));
        return new NetConnectionMetadata(muuid, color, name, locale);
    }

    @FunctionalInterface
    public interface Provider {
        @Nullable NetConnectionMetadata provide(final NetConnection connection);
    }
}
