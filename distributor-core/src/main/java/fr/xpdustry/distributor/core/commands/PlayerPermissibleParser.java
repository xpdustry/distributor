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

import fr.xpdustry.distributor.api.permission.PermissibleManager;
import fr.xpdustry.distributor.api.permission.PlayerPermissible;
import fr.xpdustry.distributor.api.util.Magik;
import java.util.Optional;

public final class PlayerPermissibleParser<C> extends PermissibleParser<C, PlayerPermissible> {

    private final PermissibleManager<PlayerPermissible> manager;

    public PlayerPermissibleParser(final PermissibleManager<PlayerPermissible> manager) {
        this.manager = manager;
    }

    @Override
    protected Optional<PlayerPermissible> findPermissible(final String name) {
        final var players = Magik.findPlayers(name);
        if (players.isEmpty() && Magik.isUuid(name)) {
            return Optional.of(this.manager.findOrCreateById(name));
        } else if (players.size() == 1) {
            return Optional.of(this.manager.findOrCreateById(players.get(0).uuid()));
        } else {
            return Optional.empty();
        }
    }
}
