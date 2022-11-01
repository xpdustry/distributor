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
package fr.xpdustry.distributor.api.manager;

import java.util.Optional;

public interface Manager<E, I> {

    default void saveAll(final Iterable<E> entities) {
        entities.forEach(this::save);
    }

    default boolean existsById(final I id) {
        return this.findById(id).isPresent();
    }

    default void deleteAll(final Iterable<E> entities) {
        entities.forEach(this::delete);
    }

    void save(final E entity);

    E findOrCreateById(final I id);

    Optional<E> findById(final I id);

    Iterable<E> findAll();

    boolean exists(final E entity);

    long count();

    void deleteById(final I id);

    void delete(final E entity);

    void deleteAll();
}
