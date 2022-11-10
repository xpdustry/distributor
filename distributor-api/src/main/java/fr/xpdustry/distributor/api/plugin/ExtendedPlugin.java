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
import arc.Core;
import arc.files.Fi;
import arc.util.CommandHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import mindustry.Vars;
import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExtendedPlugin extends Plugin {

    private final PluginDescriptor descriptor = PluginDescriptor.from(this);
    private final Path directory =
            Vars.modDirectory.child(this.getDescriptor().getName()).file().toPath();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    {
        try {
            Files.createDirectories(this.directory);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onInit() {}

    public void onServerCommandsRegistration(final CommandHandler handler) {}

    public void onClientCommandsRegistration(final CommandHandler handler) {}

    public void onLoad() {}

    public void onExit() {}

    public final Path getDirectory() {
        return this.directory;
    }

    public final PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    public final Logger getLogger() {
        return this.logger;
    }

    @Deprecated
    @Override
    public void registerServerCommands(final CommandHandler handler) {
        this.onInit();
        this.onServerCommandsRegistration(handler);

        Core.app.addListener(new ApplicationListener() {

            @Override
            public void init() {
                ExtendedPlugin.this.onLoad();
            }

            @Override
            public void dispose() {
                ExtendedPlugin.this.onExit();
            }
        });
    }

    @Deprecated
    @Override
    public void registerClientCommands(final CommandHandler handler) {
        this.onClientCommandsRegistration(handler);
    }

    @Deprecated
    @Override
    public Fi getConfig() {
        return super.getConfig();
    }

    @Deprecated
    @Override
    public void loadContent() {}

    @Deprecated
    @Override
    public void init() {}
}
