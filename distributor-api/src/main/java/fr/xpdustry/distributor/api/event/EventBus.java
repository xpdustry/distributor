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
package fr.xpdustry.distributor.api.event;

/**
 * A simple class for registering event listeners in an object-oriented way.
 */
public interface EventBus {

    /**
     * Returns an {@code EventBus} instance bound to {@link arc.Events arc's event bus}.
     */
    static EventBus mindustry() {
        return ArcEventBus.INSTANCE;
    }

    /**
     * Posts an event to the event bus.
     */
    void post(final Object event);

    /**
     * Registers an event listener.
     */
    void register(final EventBusListener listener);

    /**
     * Unregisters an event listener.
     */
    void unregister(final EventBusListener listener);
}
