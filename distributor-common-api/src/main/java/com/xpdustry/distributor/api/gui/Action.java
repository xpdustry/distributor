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

    static <T> Action with(final Key<T> key, final T value) {
        return window -> window.getState().set(key, value);
    }

    static <T> Action compute(final Key<T> key, final T def, final Function<T, T> function) {
        return window -> {
            final var value = window.getState().getOptional(key).orElse(def);
            window.getState().set(key, function.apply(value));
        };
    }

    static Action without(final Key<?> key) {
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

    static Action audience(final Consumer<Audience> consumer) {
        return view ->
                consumer.accept(DistributorProvider.get().getAudienceProvider().getPlayer(view.getViewer()));
    }

    static Action run(final Runnable runnable) {
        return view -> runnable.run();
    }

    static Action command(final String name, final String... arguments) {
        final var builder = new StringBuilder(name.length() + 1 + (arguments.length * 4));
        builder.append('/').append(name);
        for (final var argument : arguments) {
            builder.append(' ').append(argument);
        }
        final var input = builder.toString();
        return view -> Vars.netServer.clientCommands.handleMessage(input, view.getViewer());
    }

    void act(final Window window);
}
