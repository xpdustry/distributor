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
package fr.xpdustry.distributor.core.security.permission;

import fr.xpdustry.distributor.api.security.permission.PlayerPermissible;
import mindustry.Vars;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class SimplePlayerPermissible extends AbstractPermissible implements PlayerPermissible {

    private final String uuid;

    public SimplePlayerPermissible(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        if (Vars.netServer != null) {
            final var info = Vars.netServer.admins.getInfoOptional(this.uuid);
            return info == null ? "unknown" : info.lastName;
        }
        return "unknown";
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final SimplePlayerPermissible that = (SimplePlayerPermissible) o;

        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.uuid.hashCode();
        return result;
    }
}
