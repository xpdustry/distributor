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
package fr.xpdustry.distributor.api.security.permission;

import java.util.Optional;

/**
 * A manager for a specific type of permissible.
 *
 * @param <P> the type of permissible
 */
public interface PermissibleManager<P extends Permissible> {

    /**
     * Saves the given permissible.
     *
     * @param permissible the permissible to save
     */
    void save(final P permissible);

    /**
     * Saves the given permissibles in bulk.
     *
     * @param permissibles the permissibles to save
     */
    default void saveAll(final Iterable<P> permissibles) {
        permissibles.forEach(this::save);
    }

    /**
     * Returns the permissible with the given id. If not found, a new permissible is created.
     *
     * @param id the id of the permissible
     * @return the permissible or a new one
     */
    P findOrCreateById(final String id);

    /**
     * Returns the permissible with the given id. If not found, an empty optional is returned.
     *
     * @param id the id of the permissible
     * @return the permissible or an empty optional
     */
    Optional<P> findById(final String id);

    /**
     * Returns all the permissibles.
     */
    Iterable<P> findAll();

    /**
     * Checks if the permissible exists in the database.
     *
     * @param permissible the permissible to check
     * @return {@code true} if the permissible exists, {@code false} otherwise
     */
    boolean exists(final P permissible);

    /**
     * Checks if the permissible exists in the database by id.
     *
     * @param id the id of the permissible
     * @return {@code true} if the permissible exists, {@code false} otherwise
     */
    default boolean existsById(final String id) {
        return this.findById(id).isPresent();
    }

    /**
     * Returns the number of permissibles.
     */
    long count();

    /**
     * Deletes the given permissible by id if it exists.
     *
     * @param id the id of the permissible to delete
     */
    void deleteById(final String id);

    /**
     * Deletes the given permissible.
     *
     * @param permissible the permissible to delete
     */
    void delete(final P permissible);

    /**
     * Deletes all the permissibles.
     */
    void deleteAll();

    /**
     * Deletes all the given permissibles in bulk.
     *
     * @param permissibles the permissibles to delete
     */
    default void deleteAll(final Iterable<P> permissibles) {
        permissibles.forEach(this::delete);
    }
}
