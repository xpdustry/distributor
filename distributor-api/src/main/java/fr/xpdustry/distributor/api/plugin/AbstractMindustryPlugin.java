/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
import fr.xpdustry.distributor.api.event.MoreEvents;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of {@link MindustryPlugin}, without the quirks of {@link Plugin} (like the fact that
 * {@link #registerServerCommands(CommandHandler)} is called before {@link #init()}).
 */
public abstract class AbstractMindustryPlugin extends Plugin implements MindustryPlugin {

    private final PluginDescriptor descriptor = PluginDescriptor.from(this);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<PluginListener> listeners = new ArrayList<>();

    @Override
    public final PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public final Logger getLogger() {
        return this.logger;
    }

    /**
     * Returns an unmodifiable list of the listeners registered to this plugin.
     */
    protected final List<PluginListener> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    @Override
    public void addListener(final PluginListener listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already registered.");
        }
        this.listeners.add(listener);
        MoreEvents.parse(this, listener);
    }

    @Deprecated
    @Override
    public final void registerServerCommands(final CommandHandler handler) {
        MoreEvents.parse(this, this);

        try {
            Files.createDirectories(this.getDirectory());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.onInit();
        for (final var listener : AbstractMindustryPlugin.this.listeners) {
            listener.onPluginInit();
        }

        this.onServerCommandsRegistration(handler);
        for (final var listener : AbstractMindustryPlugin.this.listeners) {
            listener.onPluginServerCommandsRegistration(handler);
        }

        Core.app.addListener(new ApplicationListener() {

            @Override
            public void init() {
                AbstractMindustryPlugin.this.onLoad();
                for (final var listener : AbstractMindustryPlugin.this.listeners) {
                    listener.onPluginLoad();
                }
            }

            @Override
            public void update() {
                AbstractMindustryPlugin.this.onUpdate();
                for (final var listener : AbstractMindustryPlugin.this.listeners) {
                    listener.onPluginUpdate();
                }
            }

            @Override
            public void dispose() {
                AbstractMindustryPlugin.this.onExit();
                for (final var listener : AbstractMindustryPlugin.this.listeners) {
                    listener.onPluginExit();
                }
            }
        });
    }

    @Deprecated
    @Override
    public final void registerClientCommands(final CommandHandler handler) {
        this.onClientCommandsRegistration(handler);
        for (final var listener : AbstractMindustryPlugin.this.listeners) {
            listener.onPluginClientCommandsRegistration(handler);
        }
    }

    @Deprecated
    @Override
    public void init() {}

    @Deprecated
    @Override
    public void loadContent() {}

    @Deprecated
    @Override
    public Fi getConfig() {
        return new Fi(this.getDirectory().resolve("config.json").toFile());
    }
}
