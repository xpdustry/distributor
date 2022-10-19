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
package fr.xpdustry.distributor.persistence;

import java.util.*;

public interface PersistenceManager<E, I> {

  void save(final E entity);

  default void saveAll(final Iterable<E> entities) {
    entities.forEach(this::save);
  }

  E findOrCreateById(final I id);

  Optional<E> findById(final I id);

  Iterable<E> findAll();

  default boolean existsById(final I id) {
    return findById(id).isPresent();
  }

  boolean exists(final E entity);

  long count();

  void deleteById(final I id);

  void delete(final E entity);

  default void deleteAll(final Iterable<E> entities) {
    entities.forEach(this::delete);
  }

  void deleteAll();
}
