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

import com.xpdustry.distributor.api.key.Key;

/**
 * A function that is executed when an interaction occurs in a gui. Takes an additional parameter as the input.
 */
@FunctionalInterface
public interface BiAction<T> {

    /**
     * Returns a {@code BiAction} that wraps the given {@code Action}.
     *
     * @param action the action
     * @return the bi-action
     */
    static <T> BiAction<T> from(final Action action) {
        return (window, value) -> action.act(window);
    }

    /**
     * Returns a bi-action that sets a state entry with the bi-action input.
     *
     * @param key   the key
     * @param <T>   the type of the value
     * @return the bi-action
     */
    static <T> BiAction<T> with(final Key<T> key) {
        return (window, value) -> window.getState().set(key, value);
    }

    /**
     * Executes the bi-action.
     *
     * @param window the window
     * @param input  the input
     */
    void act(final Window window, final T input);

    /**
     * Returns a {@code BiAction} that executes this bi-action and then the given bi-action.
     *
     * @param next the next bi-action
     * @return the bi-action
     */
    default BiAction<T> then(final BiAction<T> next) {
        return (window, value) -> {
            this.act(window, value);
            next.act(window, value);
        };
    }

    /**
     * Returns a {@code BiAction} that executes this bi-action and then the given action.
     *
     * @param next the next action
     * @return the bi-action
     */
    default BiAction<T> then(final Action next) {
        return (window, value) -> {
            this.act(window, value);
            next.act(window);
        };
    }
}
