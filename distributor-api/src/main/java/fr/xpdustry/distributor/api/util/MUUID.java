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
package fr.xpdustry.distributor.api.util;

import java.util.Objects;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The mindustry identity format. A combination of a UUID and a USID.
 */
public final class MUUID {

    private final String uuid;
    private final String usid;

    private MUUID(final String uuid, final String usid) {
        this.uuid = uuid;
        this.usid = usid;
    }

    /**
     * Creates a new MUUID from a UUID and USID.
     *
     * @param uuid the UUID
     * @param usid the USID
     * @return the MUUID
     */
    public static MUUID of(final String uuid, final String usid) {
        return new MUUID(uuid, usid);
    }

    /**
     * Creates a new MUUID from a {@link Player}.
     *
     * @param player the player
     * @return the MUUID
     */
    public static MUUID of(final Player player) {
        return new MUUID(player.uuid(), player.usid());
    }

    /**
     * Creates a new MUUID from a {@link Administration.PlayerInfo}.
     *
     * @param info the player info
     * @return the MUUID
     */
    public static MUUID of(final Administration.PlayerInfo info) {
        return new MUUID(info.id, info.adminUsid);
    }

    /**
     * Returns the UUID.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Returns the USID.
     */
    public String getUsid() {
        return this.usid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid, this.usid);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (obj instanceof MUUID muuid && this.uuid.equals(muuid.uuid) && this.usid.equals(muuid.usid));
    }
}
