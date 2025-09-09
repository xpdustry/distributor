/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.catalina.security.antinsfw;

import com.xpdustry.distributor.api.event.EventSubscription;
import mindustry.gen.Building;
import mindustry.gen.Player;
import org.jspecify.annotations.Nullable;

public interface BuildingLifecycle {

    <B extends Building> EventSubscription subscribe(final Class<B> type, final Listener<B> listener);

    @FunctionalInterface
    interface Listener<B extends Building> {

        void onBuildingLifecycleEvent(final B building, final Kind kind, final @Nullable Player player);
    }

    enum Kind {
        INSERT,
        UPDATE,
        REMOVE,
    }
}
