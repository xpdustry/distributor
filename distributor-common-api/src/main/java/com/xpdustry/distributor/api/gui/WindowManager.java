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

import com.xpdustry.distributor.api.player.MUUID;
import java.util.Collection;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A factory creating {@link Window} instances.
 */
public interface WindowManager {

    /**
     * Creates a new window for the given viewer.
     *
     * @param viewer the viewer
     * @return the window
     */
    Window create(final Player viewer);

    /**
     * Creates a new window for the given parent window.
     *
     * @param parent the parent window
     * @return the window
     */
    Window create(final Window parent);

    /**
     * Returns the active windows.
     */
    Collection<Window> getActiveWindows();

    /**
     * Returns the active window of the given viewer.
     *
     * @param viewer the viewer
     * @return the active window
     */
    default @Nullable Window getActiveWindow(final Player viewer) {
        final var target = MUUID.from(viewer);
        return getActiveWindows().stream()
                .filter(window -> MUUID.from(window.getViewer()).equals(target))
                .findFirst()
                .orElse(null);
    }

    /**
     * Disposes this window manager.
     */
    default void dispose() {}
}
