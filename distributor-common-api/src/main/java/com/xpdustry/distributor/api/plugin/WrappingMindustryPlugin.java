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
package com.xpdustry.distributor.api.plugin;

import arc.util.CommandHandler;
import java.nio.file.Path;
import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

record WrappingMindustryPlugin(Plugin plugin) implements MindustryPlugin {

    @Override
    public void onInit() {
        this.plugin.init();
    }

    @Override
    public void onClientCommandsRegistration(final CommandHandler handler) {
        this.plugin.registerClientCommands(handler);
    }

    @Override
    public void onServerCommandsRegistration(final CommandHandler handler) {
        this.plugin.registerServerCommands(handler);
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(this.plugin.getClass());
    }

    @Override
    public Path getDirectory() {
        return this.plugin.getConfig().parent().file().toPath();
    }

    @Override
    public PluginMetadata getMetadata() {
        return PluginMetadata.from(this.plugin);
    }
}
