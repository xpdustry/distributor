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
package fr.xpdustry.distributor.api.plugin;

import arc.ApplicationListener;

public class PluginApplicationListener implements ApplicationListener, PluginAware {

    private final ExtendedPlugin plugin;

    public PluginApplicationListener(final ExtendedPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.plugin.onInit();
        for (final var listener : this.plugin.getListeners()) {
            listener.onPluginInit();
        }
    }

    @Override
    public void dispose() {
        this.plugin.onExit();
        for (final var listener : this.plugin.getListeners()) {
            listener.onPluginExit();
        }
    }

    @Override
    public final ExtendedPlugin getPlugin() {
        return this.plugin;
    }
}
