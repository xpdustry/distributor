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
package com.xpdustry.distributor.api.gui;

import java.util.ArrayList;
import java.util.List;

final class Actions {

    private Actions() {}

    record CompositeAction(List<Action> actions) implements Action {

        public CompositeAction(final List<Action> actions) {
            this.actions = List.copyOf(actions);
        }

        @Override
        public void act(final Window window) {
            for (final var action : this.actions) {
                action.act(window);
            }
        }

        @Override
        public Action then(final Action next) {
            final var list = new ArrayList<>(this.actions);
            list.add(next);
            return new CompositeAction(list);
        }
    }
}
