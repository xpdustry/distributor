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
import java.util.concurrent.*;
import java.util.function.*;

// TODO Decide whether to keep the completable futures or not
public interface PersistenceManager<E, I> {

  CompletableFuture<E> findOrCreateById(final I id);

  CompletableFuture<Optional<E>> findById(final I id);

  CompletableFuture<List<E>> findAll();

  CompletableFuture<Long> count();

  default CompletableFuture<Boolean> existsById(final I id) {
    return findById(id).thenApply(Optional::isPresent);
  }

  CompletableFuture<Void> save(final E entity);

  CompletableFuture<Void> deleteById(I id);

  CompletableFuture<Void> delete(final E entity);

  CompletableFuture<Void> deleteAll();

  default CompletableFuture<Void> modify(final I id, final Consumer<E> action) {
    return findOrCreateById(id)
      .thenApplyAsync(entity -> {
        action.accept(entity);
        return entity;
      })
      .thenCompose(this::save);
  }
}
