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
package fr.xpdustry.distributor.core.permission;

import fr.xpdustry.distributor.api.permission.PlayerPermissible;
import mindustry.Vars;

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
}
