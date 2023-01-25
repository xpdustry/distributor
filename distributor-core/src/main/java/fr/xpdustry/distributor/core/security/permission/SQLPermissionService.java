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
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import mindustry.Vars;

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
        try (final var input = this.getClass().getResourceAsStream("/schemas/permission.sql")) {
            if (input == null) {
                throw new IllegalStateException("Missing schema file.");
            }
            connectionFactory.executeScript(input);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.configuration = configuration;
        this.validator = validator;
        this.players = new SQLPlayerPermissibleManager(connectionFactory);
        this.groups = new SQLGroupPermissibleManager(connectionFactory);
    }

    @Override
    public Tristate getPermission(final MUUID muuid, final String permission) {
        if (!this.validator.isValid(muuid)) {
            return Tristate.FALSE;
        }

        if (!this.configuration.isAdminStatusIgnored()
                && Vars.netServer.admins.isAdmin(muuid.getUuid(), muuid.getUsid())) {
            return Tristate.TRUE;
        }

        final var perm = permission.toLowerCase(Locale.ROOT);
        var state = Tristate.UNDEFINED;
        final var visited = new HashSet<String>();
        final Queue<Permissible> queue = new ArrayDeque<>();
        final var player = this.players.findById(muuid.getUuid());
        final var primary = this.groups.findById(this.configuration.getPermissionPrimaryGroup());

        if (player.isPresent()) {
            queue.add(player.get());
        } else if (primary.isPresent()) {
            queue.add(primary.get());
        } else {
            return state;
        }

        while (!queue.isEmpty()) {
            final var holder = queue.remove();
            state = holder.getPermission(perm);
            if (state != Tristate.UNDEFINED) {
                break;
            }

            holder.getParentGroups().stream()
                    .filter(visited::add)
                    .map(this.groups::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(GROUP_COMPARATOR)
                    .forEach(queue::add);

            if (queue.isEmpty()
                    && !visited.add(this.configuration.getPermissionPrimaryGroup())
                    && primary.isPresent()) {
                queue.add(primary.get());
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
}
