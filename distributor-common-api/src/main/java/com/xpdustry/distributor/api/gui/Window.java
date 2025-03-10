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

import com.xpdustry.distributor.api.key.MutableKeyContainer;
import mindustry.gen.Player;
import org.jspecify.annotations.Nullable;

/**
 * Represents a window. A container for the pane and state of a GUI.
 */
public interface Window {

    /**
     * Returns the viewer of this window.
     */
    Player getViewer();

    /**
     * Returns the state of this window.
     */
    MutableKeyContainer getState();

    /**
     * Returns the parent window of this window.
     */
    @Nullable Window getParent();

    /**
     * Returns whether this window is active.
     */
    boolean isActive();

    /**
     * Shows this window.
     */
    void show();

    /**
     * Hides this window.
     */
    void hide();
}
