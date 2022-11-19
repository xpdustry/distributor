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

import fr.xpdustry.distributor.api.permission.GroupPermissible;
import fr.xpdustry.distributor.api.permission.Permissible;
import fr.xpdustry.distributor.api.permission.PermissibleManager;
import fr.xpdustry.distributor.api.permission.PermissionService;
import fr.xpdustry.distributor.api.permission.PlayerPermissible;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import mindustry.Vars;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class SimplePermissionService implements PermissionService {

    private static final Comparator<GroupPermissible> GROUP_COMPARATOR =
            Comparator.comparing(GroupPermissible::getWeight).reversed();

    private final YamlConfigurationLoader loader;
    private final SimplePlayerPermissibleManager players;
    private final SimpleGroupPermissibleManager groups;

    private String primaryGroup;
    private boolean verifyAdmin;

    public SimplePermissionService(final Path directory) {
        this.loader = YamlConfigurationLoader.builder()
                .indent(2)
                .path(directory.resolve("settings.yaml"))
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        this.players = new SimplePlayerPermissibleManager(directory.resolve("players.yaml"));
        this.groups = new SimpleGroupPermissibleManager(directory.resolve("groups.yaml"));

        try {
            final var root = this.loader.load();
            this.primaryGroup = root.node("primary-group").getString("default");
            this.verifyAdmin = root.node("verify-admin").getBoolean(true);
        } catch (final ConfigurateException e) {
            throw new RuntimeException("Unable to load the permissions.", e);
        }
    }

    @Override
    public Tristate getPermission(final MUUID muuid, final String permission) {
        if (this.verifyAdmin) {
            final var info = Vars.netServer.admins.getInfoOptional(muuid.getUuid());
            if (info != null && info.admin) {
                return Tristate.TRUE;
            }
        }

        final var perm = permission.toLowerCase(Locale.ROOT);
        var state = Tristate.UNDEFINED;
        final var visited = new HashSet<String>();
        final Queue<Permissible> queue = new ArrayDeque<>();
        final var player = this.players.findById(muuid.getUuid());
        final var primary = this.groups.findById(this.primaryGroup);

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

            if (queue.isEmpty() && !visited.add(this.primaryGroup) && primary.isPresent()) {
                queue.add(primary.get());
            }
        }

        return state;
    }

    @Override
    public String getPrimaryGroup() {
        return this.primaryGroup;
    }

    @Override
    public void setPrimaryGroup(final String group) {
        this.primaryGroup = group;
        this.save();
    }

    @Override
    public boolean getVerifyAdmin() {
        return this.verifyAdmin;
    }

    @Override
    public void setVerifyAdmin(final boolean verify) {
        this.verifyAdmin = verify;
        this.save();
    }

    @Override
    public PermissibleManager<PlayerPermissible> getPlayerPermissionManager() {
        return this.players;
    }

    @Override
    public PermissibleManager<GroupPermissible> getGroupPermissionManager() {
        return this.groups;
    }

    private void save() {
        try {
            final var root = this.loader.createNode();
            root.node("primary-group").set(this.primaryGroup);
            root.node("verify-admin").set(this.verifyAdmin);
            this.loader.save(root);
        } catch (final ConfigurateException e) {
            throw new RuntimeException("Failed to save the permission manager settings.", e);
        }
    }
}
