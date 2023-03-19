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

import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.security.permission.GroupPermissible;
import fr.xpdustry.distributor.api.security.permission.Permissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.api.security.permission.PermissionService;
import fr.xpdustry.distributor.api.security.permission.PlayerPermissible;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.DistributorConfiguration;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import mindustry.Vars;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class SQLPermissionService implements PermissionService {

    private static final Comparator<GroupPermissible> GROUP_COMPARATOR =
            Comparator.comparing(GroupPermissible::getWeight).reversed();

    private final DistributorConfiguration configuration;
    private final PlayerValidator validator;
    private final SQLPlayerPermissibleManager players;
    private final SQLGroupPermissibleManager groups;

    public SQLPermissionService(
            final DistributorConfiguration configuration,
            final ConnectionFactory connectionFactory,
            final PlayerValidator validator) {
        Permissibles.createDatabase(connectionFactory);
        this.configuration = configuration;
        this.validator = validator;
        this.players = new SQLPlayerPermissibleManager(connectionFactory);
        this.groups = new SQLGroupPermissibleManager(connectionFactory);
    }

    @Override
    public Tristate getPlayerPermission(final MUUID muuid, final String permission) {
        if (!this.validator.isValid(muuid)) {
            return Tristate.FALSE;
        }

        if (!this.configuration.isAdminIgnored() && Vars.netServer.admins.isAdmin(muuid.getUuid(), muuid.getUsid())) {
            return Tristate.TRUE;
        }

        return this.getPlayerPermission(muuid.getUuid(), permission);
    }

    @Override
    public Tristate getPlayerPermission(final String uuid, final String permission) {
        final var player = this.players.findById(uuid);
        final var query = permission.toLowerCase(Locale.ROOT);
        var state = Tristate.UNDEFINED;

        if (!Permissible.PERMISSION_PATTERN.matcher(query).matches()) {
            return Tristate.UNDEFINED;
        }

        final Permissible permissible;
        final List<GroupPermissible> parents;

        if (player.isPresent()) {
            permissible = player.get();
            parents = this.getParents(player.get(), this.configuration.getPermissionPrimaryGroup());
        } else {
            final var primary = this.groups.findById(this.configuration.getPermissionPrimaryGroup());
            if (primary.isPresent()) {
                permissible = primary.get();
                parents = this.getParents(primary.get(), null);
            } else {
                return state;
            }
        }

        state = permissible.getPermission(query);
        if (state != Tristate.UNDEFINED) {
            return state;
        }

        for (final var parent : parents) {
            state = parent.getPermission(query);
            if (state != Tristate.UNDEFINED) {
                break;
            }
        }

        return state;
    }

    @Override
    public Tristate getGroupPermission(final String group, final String permission) {
        final var permissible = this.groups.findById(group);
        final var query = permission.toLowerCase(Locale.ROOT);

        if (!Permissible.PERMISSION_PATTERN.matcher(query).matches()) {
            return Tristate.UNDEFINED;
        }

        var state = Tristate.UNDEFINED;

        if (permissible.isPresent()) {
            state = permissible.get().getPermission(query);
        } else {
            return state;
        }

        if (state != Tristate.UNDEFINED) {
            return state;
        }

        final var parents = this.getParents(permissible.get(), null);
        for (final var parent : parents) {
            state = parent.getPermission(query);
            if (state != Tristate.UNDEFINED) {
                break;
            }
        }

        return state;
    }

    @Override
    public PermissibleManager<PlayerPermissible> getPlayerPermissionManager() {
        return this.players;
    }

    @Override
    public PermissibleManager<GroupPermissible> getGroupPermissionManager() {
        return this.groups;
    }

    private List<GroupPermissible> getParents(final Permissible permissible, final @Nullable String primary) {
        final Set<String> visited = new HashSet<>(permissible.getParentGroups());
        final List<GroupPermissible> parents = new ArrayList<>(permissible.getParentGroups().stream()
                .map(this.groups::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());

        for (int i = 0; i < parents.size(); i++) {
            final var parent = parents.get(i);
            parent.getParentGroups().stream()
                    .filter(visited::add)
                    .map(this.groups::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(parents::add);

            if (primary != null && parents.size() - 1 == i && !visited.add(primary)) {
                final var primaryGroup = this.groups.findById(primary);
                primaryGroup.ifPresent(parents::add);
            }
        }

        parents.sort(GROUP_COMPARATOR);

        return parents;
    }
}
