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

import com.xpdustry.distributor.api.key.TypedKey;

@FunctionalInterface
public interface Action {

    static Action of(final Action... actions) {
        return (window) -> {
            for (final var action : actions) {
                action.act(window);
            }
        };
    }

    static Action none() {
        return window -> {};
    }

    static <T> Action with(final TypedKey<T> key, final T value) {
        return window -> window.getState().set(key, value);
    }

    static Action without(final TypedKey<?> key) {
        return window -> window.getState().remove(key);
    }

    static Action back() {
        return back(1);
    }

    static Action back(final int depth) {
        return window -> {
            var current = window;
            var i = depth;
            while (current != null && i-- > 0) {
                current.hide();
                current = current.getParent();
            }
            if (current != null) {
                current.show();
            }
        };
    }

    static Action hideAll() {
        return window -> {
            var current = window;
            while (current != null) {
                current.hide();
                current = current.getParent();
            }
        };
    }

    void act(final Window window);
}
