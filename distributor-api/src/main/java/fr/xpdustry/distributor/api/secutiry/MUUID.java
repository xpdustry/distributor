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
package fr.xpdustry.distributor.api.secutiry;

import fr.xpdustry.distributor.api.util.Magik;
import java.util.Objects;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * TODO More complete description
 * Mindustry identity format.
 */
public final class MUUID {

    private final String uuid;
    private final String usid;

    private MUUID(final String uuid, final String usid) {
        if (!Magik.isUuid(Objects.requireNonNull(uuid, "uuid"))) {
            throw new IllegalArgumentException(uuid + " is not a valid uuid.");
        }
        if (!Magik.isUsid(Objects.requireNonNull(usid, "usid"))) {
            throw new IllegalArgumentException(uuid + " is not a valid usid.");
        }
        this.uuid = uuid;
        this.usid = usid;
    }

    public static MUUID of(final String uuid, final String usid) {
        return new MUUID(uuid, usid);
    }

    public static MUUID of(final Player player) {
        return new MUUID(player.uuid(), player.usid());
    }

    public static MUUID of(final Administration.PlayerInfo info) {
        return new MUUID(info.id, info.adminUsid);
    }

    public String getUuid() {
        return this.uuid;
    }

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
