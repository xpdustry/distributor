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
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import mindustry.Vars;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public final class SQLPermissionService implements PermissionService {

    private static final Comparator<GroupPermissible> GROUP_COMPARATOR =
            Comparator.comparing(GroupPermissible::getWeight).reversed();

    private final ConnectionFactory connectionFactory;
    private final SQLPlayerPermissibleManager players;
    private final SQLGroupPermissibleManager groups;
    private final SQLPermissibleOptionManager options;

    public SQLPermissionService(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;

        this.connectionFactory.withConsumer(con -> {
            try (final var input = this.getClass().getResourceAsStream("/schemas/permission.sql");
                    final var statements = con.createStatement()) {
                if (input == null) {
                    throw new IllegalStateException("Missing schema file.");
                }
                for (final var statement : this.readStatements(input)) {
                    statements.addBatch(
                            this.connectionFactory.getStatementProcessor().apply(statement));
                }
                statements.executeBatch();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.players = new SQLPlayerPermissibleManager(this.connectionFactory);
        this.groups = new SQLGroupPermissibleManager(this.connectionFactory);
        this.options = new SQLPermissibleOptionManager(this.connectionFactory);
    }

    @Override
    public Tristate getPermission(final MUUID muuid, final String permission) {
        if (this.getVerifyAdmin()) {
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
        final var primary = this.groups.findById(this.getPrimaryGroup());

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

            if (queue.isEmpty() && !visited.add(this.getPrimaryGroup()) && primary.isPresent()) {
                queue.add(primary.get());
            }
        }

        return state;
    }

    @Override
    public String getPrimaryGroup() {
        return this.options.get("primary-group", String.class).orElse("default");
    }

    @Override
    public void setPrimaryGroup(final String group) {
        this.options.set("primary-group", group);
    }

    @Override
    public boolean getVerifyAdmin() {
        return this.options.get("verify-admin", Boolean.class).orElse(true);
    }

    @Override
    public void setVerifyAdmin(final boolean verify) {
        this.options.set("verify-admin", verify);
    }

    @Override
    public PermissibleManager<PlayerPermissible> getPlayerPermissionManager() {
        return this.players;
    }

    @Override
    public PermissibleManager<GroupPermissible> getGroupPermissionManager() {
        return this.groups;
    }

    private List<String> readStatements(final InputStream stream) throws IOException {
        final List<String> statements = new ArrayList<>();

        try (final var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            var builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                builder.append(line);

                // check for end of declaration
                if (line.endsWith(";")) {
                    builder.deleteCharAt(builder.length() - 1);

                    final String result = builder.toString().trim();
                    if (!result.isEmpty()) {
                        statements.add(result);
                    }

                    // reset
                    builder = new StringBuilder();
                }
            }
        }

        return statements;
    }
}
