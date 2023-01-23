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
package fr.xpdustry.distributor.core.commands;

import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.api.security.permission.PlayerPermissible;
import fr.xpdustry.distributor.core.DistributorPlugin;
import fr.xpdustry.distributor.core.commands.parser.PlayerPermissibleParser;

public final class PlayerPermissibleCommands extends PermissibleCommands<PlayerPermissible> {

    public PlayerPermissibleCommands(
            final DistributorPlugin distributor, final PermissibleManager<PlayerPermissible> manager) {
        super(distributor, manager, PlayerPermissibleParser::new);
    }

    @Override
    protected String getPermissibleCategory() {
        return "player";
    }

    @Override
    protected Class<PlayerPermissible> getPermissibleClass() {
        return PlayerPermissible.class;
    }
}
