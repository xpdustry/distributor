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

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.key.Key;
import java.util.function.Consumer;
import java.util.function.Function;
import mindustry.Vars;

/**
 * A function that is executed when an interaction occurs in a gui.
 */
@FunctionalInterface
public interface Action {

    /**
     * Returns an action that does nothing.
     */
    static Action none() {
        return window -> {};
    }

    /**
     * Returns an action that sets a state entry with the given value.
     *
     * @param key   the key
     * @param value the value
     * @param <T>   the type of the value
     * @return the action
     */
    static <T> Action with(final Key<T> key, final T value) {
        return window -> window.getState().set(key, value);
    }

    /**
     * Returns an action that computes a state entry.
     * If not present, the default value is used.
     *
     * @param key      the key
     * @param def      the default value
     * @param function the function to compute the value
     * @param <T>      the type of the value
     * @return the action
     */
    static <T> Action compute(final Key<T> key, final T def, final Function<T, T> function) {
        return window -> {
            final var value = window.getState().getOptional(key).orElse(def);
            window.getState().set(key, function.apply(value));
        };
    }

    /**
     * Returns an action that computes a state entry if it exists.
     *
     * @param key      the key
     * @param function the function to compute the value
     * @param <T>      the type of the value
     * @return the action
     */
    static <T> Action compute(final Key<T> key, final Function<T, T> function) {
        return window -> {
            window.getState().getOptional(key).ifPresent(value -> window.getState()
                    .set(key, function.apply(value)));
        };
    }

    /**
     * Returns an action that removes a state entry.
     *
     * @param key the key
     * @return the action
     */
    static Action without(final Key<?> key) {
        return window -> window.getState().remove(key);
    }

    /**
     * Returns an action that goes back to the previous window.
     */
    static Action back() {
        return back(1);
    }

    /**
     * Returns an action that goes back to the window at the given depth.
     *
     * @param depth the depth
     * @return the action
     */
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

    /**
     * Returns an action that hides all windows in the hierarchy.
     */
    static Action hideAll() {
        return window -> {
            var current = window;
            while (current != null) {
                current.hide();
                current = current.getParent();
            }
        };
    }

    /**
     * Returns an action mapping the viewer to an {@link Audience}.
     *
     * @param consumer the sub action running on the audience
     * @return the action
     */
    static Action audience(final Consumer<Audience> consumer) {
        return window ->
                consumer.accept(DistributorProvider.get().getAudienceProvider().getPlayer(window.getViewer()));
    }

    /**
     * Returns an action that runs the given runnable.
     *
     * @param runnable the runnable
     * @return the action
     */
    static Action run(final Runnable runnable) {
        return window -> runnable.run();
    }

    /**
     * Returns an action that invoke a command for the viewer.
     *
     * @param name the command name
     * @param arguments the command arguments
     * @return the action
     */
    static Action command(final String name, final String... arguments) {
        final var builder = new StringBuilder(name.length() + 1 + (arguments.length * 4));
        builder.append('/').append(name);
        for (final var argument : arguments) builder.append(' ').append(argument);
        final var input = builder.toString();
        return window -> Vars.netServer.clientCommands.handleMessage(input, window.getViewer());
    }

    /**
     * Returns an action that creates and shows a new window with the given manager.
     *
     * @param manager the window manager
     * @return the action
     */
    static Action show(final WindowManager manager) {
        return window -> manager.create(window.getViewer()).show();
    }

    /**
     * Executes the action.
     *
     * @param window the window
     */
    void act(final Window window);

    /**
     * Returns a new action that first executes this action and then the given action.
     *
     * @param next the action to execute after this action
     * @return the new action
     */
    default Action then(final Action next) {
        return window -> {
            this.act(window);
            next.act(window);
        };
    }
}
