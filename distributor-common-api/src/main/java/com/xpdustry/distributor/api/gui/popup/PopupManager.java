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
package com.xpdustry.distributor.api.gui.popup;

import com.xpdustry.distributor.api.gui.transform.TransformerWindowManager;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.time.Duration;

/**
 * A window manager for popups windows.
 */
public interface PopupManager extends TransformerWindowManager<PopupPane> {

    static PopupManager create(final MindustryPlugin plugin) {
        return new PopupManagerImpl(plugin);
    }

    /**
     * Returns the update interval of the popup windows.
     */
    Duration getUpdateInterval();

    /**
     * Sets the update interval of the popup windows.
     *
     * @param updateInterval the update interval
     */
    void setUpdateInterval(final Duration updateInterval);
}
