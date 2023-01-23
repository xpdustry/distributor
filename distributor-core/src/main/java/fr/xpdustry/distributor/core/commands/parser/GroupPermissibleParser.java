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
package fr.xpdustry.distributor.core.commands.parser;

import fr.xpdustry.distributor.api.security.permission.GroupPermissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import java.util.Locale;
import java.util.Optional;

public final class GroupPermissibleParser<C> extends PermissibleParser<C, GroupPermissible> {

    private final PermissibleManager<GroupPermissible> manager;

    public GroupPermissibleParser(final PermissibleManager<GroupPermissible> manager) {
        this.manager = manager;
    }

    @Override
    protected Optional<GroupPermissible> findPermissible(final String name) {
        return this.manager.findById(name.toLowerCase(Locale.ROOT));
    }
}
