/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.security.permission;

import fr.xpdustry.distributor.api.util.Tristate;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents an entity that can be assigned permissions.
 */
public interface Permissible {

    /**
     * Regex pattern used to validate permission strings.
     * <p>
     * <strong>Notes:</strong>
     * <blockquote>
     * Permission strings are composed of a series of nodes separated by dots with alphanumeric characters and minus
     * signs, such as {@code "plugin.command"}.
     * <br>
     * A parent node is always overridden by a child node, such as {@code "plugin.command"} overriding {@code "plugin"}.
     * <br>
     * Wildcards are also allowed, but they currently have the same effect as a normal node, such as {@code "plugin.command.*"} equals {@code "plugin.command"}.
     * <br>
     * The only relevant use of wildcards is the root permission {@code "*"} permission. It
     * allows you to set a default value for all permissions.
     * </blockquote>
     */
    String PERMISSION_REGEX = "^(\\*|[a-z\\d\\-]+)(\\.(\\*|[a-z\\d\\-]+))*$";

    Pattern PERMISSION_PATTERN = Pattern.compile(PERMISSION_REGEX);

    /**
     * Returns the name of this permissible.
     */
    String getName();

    /**
     * Returns the state for a given permission.
     * <ul>
     *     <li>{@link Tristate#TRUE} if the permission is explicitly granted.</li>
     *     <li>{@link Tristate#FALSE} if the permission is explicitly denied.</li>
     *     <li>{@link Tristate#UNDEFINED} if the permission is not set or it does not match the
     *     {@link #PERMISSION_REGEX regex}.</li>
     * </ul>
     *
     * @param permission the permission string
     * @return the state of the permission
     */
    Tristate getPermission(final String permission);

    /**
     * Sets the state for a given permission.
     * <ul>
     *     <li>{@link Tristate#TRUE} to explicitly grant the permission.</li>
     *     <li>{@link Tristate#FALSE} to explicitly deny the permission.</li>
     *     <li>{@link Tristate#UNDEFINED} to remove the permission.</li>
     * </ul>
     *
     * @param permission the permission string
     * @param state      the state of the permission
     */
    void setPermission(final String permission, final Tristate state);

    /**
     * Sets the state for a given permission.
     * <ul>
     *     <li>{@code true} to explicitly grant the permission.</li>
     *     <li>{@code false} to explicitly deny the permission.</li>
     * </ul>
     *
     * @param permission the permission string
     * @param state      the state of the permission
     */
    default void setPermission(final String permission, final boolean state) {
        this.setPermission(permission, Tristate.of(state));
    }

    /**
     * Returns the permissions of this permissible as a map.
     */
    Map<String, Boolean> getPermissions();

    /**
     * Sets the permissions of this permissible.
     *
     * @param permissions the permissions to set
     */
    void setPermissions(final Map<String, Boolean> permissions);

    /**
     * Returns the parents of this permissible.
     */
    Collection<String> getParentGroups();

    /**
     * Sets the parents of this permissible.
     *
     * @param parents the groups to set
     */
    void setParentGroups(final Collection<String> parents);

    /**
     * Adds a parent to this permissible.
     */
    void addParentGroup(final String group);

    /**
     * Removes a parent from this permissible.
     */
    void removeParentGroup(final String group);
}
