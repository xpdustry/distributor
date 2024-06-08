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
package com.xpdustry.distributor.api.gui.transform;

import com.xpdustry.distributor.api.gui.Pane;
import com.xpdustry.distributor.api.key.MutableKeyContainer;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import mindustry.gen.Player;
import org.immutables.value.Value;

@FunctionalInterface
public interface Transformer<P extends Pane> {

    void transform(final Context<P> context);

    @DistributorDataClass
    @Value.Immutable
    interface Context<P extends Pane> {

        static <P extends Pane> Context<P> of(final P pane, final MutableKeyContainer state, final Player viewer) {
            return ContextImpl.of(pane, state, viewer);
        }

        P getPane();

        MutableKeyContainer getState();

        Player getViewer();
    }
}
